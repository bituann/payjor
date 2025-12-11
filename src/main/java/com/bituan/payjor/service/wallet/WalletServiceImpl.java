package com.bituan.payjor.service.wallet;

import com.bituan.payjor.exception.BadRequestException;
import com.bituan.payjor.exception.InsufficientBalanceException;
import com.bituan.payjor.model.entity.Transaction;
import com.bituan.payjor.model.entity.User;
import com.bituan.payjor.model.entity.Wallet;
import com.bituan.payjor.model.enums.TransactionStatus;
import com.bituan.payjor.model.enums.TransactionType;
import com.bituan.payjor.model.request.WalletTransferRequest;
import com.bituan.payjor.model.request.paystack.InitPaymentRequest;
import com.bituan.payjor.model.response.paystack.InitPaymentResponse;
import com.bituan.payjor.model.response.paystack.VerifyPaymentResponse;
import com.bituan.payjor.model.response.wallet.WalletBalanceResponse;
import com.bituan.payjor.model.response.wallet.WalletDepositResponse;
import com.bituan.payjor.model.response.wallet.WalletTransactionResponse;
import com.bituan.payjor.model.response.wallet.WalletTransferResponse;
import com.bituan.payjor.repository.TransactionRepository;
import com.bituan.payjor.repository.UserRepository;
import com.bituan.payjor.repository.WalletRepository;
import com.bituan.payjor.service.paystack.PayStackService;
import com.bituan.payjor.service.user.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService{

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final PayStackService payStackService;

    @Value("${paystack.secret-key}")
    private String secretKey;

    @Override
    public WalletTransferResponse transfer(WalletTransferRequest request) {
        User user = userRepository.findByEmail(UserService.getAuthenticatedUser().getEmail()).orElseThrow();

        // verify amount
        if (request.getAmount() <= 0) {
            throw new BadRequestException("Invalid amount");
        }

        // check & verify sufficient balance
        if (user.getWallet().getBalance() < request.getAmount()) {
            throw new InsufficientBalanceException("Insufficient Balance");
        }

        // find recipient wallet
        Wallet recipientWallet = walletRepository.findByNumber(request.getWalletNumber())
                .orElseThrow(() -> new BadRequestException("This account number does not exist or is invalid"));

        // create reference
        String reference = UUID.randomUUID().toString().replace("-", "");

        // create transaction
        Transaction transaction = Transaction.builder()
                .recipient(recipientWallet.getOwner())
                .type(TransactionType.TRANSFER)
                .sender(user)
                .status(TransactionStatus.PENDING)
                .amount(request.getAmount())
                .reference(reference)
                .build();

        // deduct amount from user
        Wallet userWallet = user.getWallet();
        userWallet.setBalance(userWallet.getBalance() - request.getAmount());
        walletRepository.save(userWallet);

        // credit amount to recipient
        recipientWallet.setBalance(recipientWallet.getBalance() + request.getAmount());
        walletRepository.save(recipientWallet);

        // update transaction status & save
        transaction.setStatus(TransactionStatus.SUCCESS);
        transactionRepository.save(transaction);


        return WalletTransferResponse.builder()
                .status(TransactionStatus.SUCCESS)
                .message("Transfer completed")
                .build();
    }

    @Override
    public WalletBalanceResponse getBalance() {
        double balance = UserService.getAuthenticatedUser().getWallet().getBalance();

        return WalletBalanceResponse.builder()
                .balance(balance)
                .build();
    }

    @Override
    public List<WalletTransactionResponse> getAllTransactions() {
        User user = userRepository.findByEmail(UserService.getAuthenticatedUser().getEmail()).orElseThrow();

        List<Transaction> transactions = transactionRepository.findBySenderOrRecipient(user, user);

        return transactions.stream().map(transaction -> WalletTransactionResponse.builder()
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .status(transaction.getStatus().name())
                .build()
        ).toList();
    }

    @Override
    public WalletTransactionResponse verifyDepositStatus(String reference) {
        VerifyPaymentResponse paymentStatus = payStackService.verifyPayment(reference);

        return WalletTransactionResponse.builder()
                .reference(reference)
                .status(paymentStatus.getData().getStatus())
                .amount(paymentStatus.getData().getAmount())
                .build();
    }

    @Override
    public WalletDepositResponse deposit(int amount) {
        User user = userRepository.findByEmail(UserService.getAuthenticatedUser().getEmail()).orElseThrow();

        // generate reference
        String reference = UUID.randomUUID().toString().replace("-", "");

        InitPaymentRequest request = InitPaymentRequest.builder()
                .amount(amount)
                .email(UserService.getAuthenticatedUser().getEmail())
                .reference(reference)
                .build();

        InitPaymentResponse paymentResponse = payStackService.initializePayment(request);

        // save pending transaction
        Transaction transaction = Transaction.builder()
                .reference(paymentResponse.getData().getReference())
                .sender(null)
                .type(TransactionType.DEPOSIT)
                .status(TransactionStatus.PENDING)
                .amount(request.getAmount())
                .recipient(user)
                .build();

        transactionRepository.save(transaction);

        return WalletDepositResponse.builder()
                .reference(paymentResponse.getData().getReference())
                .authorizationUrl(paymentResponse.getData().getAuthorization_url())
                .build();
    }

    @Override
    public boolean handleWebHook(String signature, String payload) {

        // Validate Signature
        if (!isValidSignature(payload, signature)) {
            throw new BadRequestException("Invalid signature");
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode webhookEvent = objectMapper.readTree(payload);

            String eventType = webhookEvent.get("event").asText();
            JsonNode data = webhookEvent.get("data");


            // Handle different event types (e.g., "charge.success", "transfer.success")
            if (!"charge.success".equals(eventType)) {
                return false;
            }

            // update transaction
            String reference = data.get("reference").asText();

            Transaction transaction = transactionRepository.findByReference(reference).orElse(null);

            if (transaction == null) {
                return false;
            }

            transaction.setStatus(TransactionStatus.SUCCESS);

            transactionRepository.save(transaction);

            // update wallet balance
            String email = data.get("customer").get("email").asText();
            User user = userRepository.findByEmail(email).orElseThrow();
            Wallet userWallet = user.getWallet();

            userWallet.setBalance(userWallet.getBalance() + data.get("amount").asDouble());

            userRepository.save(user);

            return true;

        } catch (Exception e) {
            return false;
        }
    }



    private boolean isValidSignature(String payload, String signature) {
        try {
            Mac sha512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA512");
            sha512.init(keySpec);

            String hash = HexFormat.of().formatHex(sha512.doFinal(payload.getBytes()));

            return hash.equals(signature);
        } catch (Exception e) {
            return false;
        }
    }
}
