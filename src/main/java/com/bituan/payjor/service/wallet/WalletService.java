package com.bituan.payjor.service.wallet;

import com.bituan.payjor.model.request.WalletTransferRequest;
import com.bituan.payjor.model.request.paystack.InitPaymentRequest;
import com.bituan.payjor.model.response.wallet.WalletBalanceResponse;
import com.bituan.payjor.model.response.wallet.WalletDepositResponse;
import com.bituan.payjor.model.response.wallet.WalletTransactionResponse;
import com.bituan.payjor.model.response.wallet.WalletTransferResponse;

import java.util.List;
import java.util.UUID;

public interface WalletService {
    WalletTransferResponse transfer(WalletTransferRequest request);
    WalletBalanceResponse getBalance();
    List<WalletTransactionResponse> getAllTransactions();
    WalletTransactionResponse getTransactionStatus(UUID reference);
    WalletDepositResponse deposit(int amount);
}
