package com.bituan.payjor.service.wallet;

import com.bituan.payjor.model.entity.Transaction;
import com.bituan.payjor.model.entity.User;
import com.bituan.payjor.model.entity.Wallet;
import com.bituan.payjor.model.enums.TransactionStatus;
import com.bituan.payjor.model.enums.TransactionType;
import com.bituan.payjor.model.request.WalletTransferRequest;
import com.bituan.payjor.model.response.wallet.WalletBalanceResponse;
import com.bituan.payjor.model.response.wallet.WalletTransferResponse;
import com.bituan.payjor.repository.TransactionRepository;
import com.bituan.payjor.repository.WalletRepository;
import com.bituan.payjor.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService{

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public WalletTransferResponse transfer(WalletTransferRequest request) {
        User user = UserService.getAuthenticatedUser();

        // verify amount
        if (request.getAmount() <= 0) {
            throw new RuntimeException("Invalid amount");
        }

        // check & verify sufficient balance
        if (user.getWallet().getBalance() < request.getAmount()) {
            throw new RuntimeException("Insufficient Balance");
        }

        // find recipient wallet
        Wallet recipientWallet = walletRepository.findByNumber(request.getWalletNumber()).orElseThrow(RuntimeException::new);

        // create transaction
        Transaction transaction = Transaction.builder()
                .recipient(recipientWallet.getOwner())
                .type(TransactionType.TRANSFER)
                .user(user)
                .status(TransactionStatus.PENDING)
                .wallet(user.getWallet())
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
}
