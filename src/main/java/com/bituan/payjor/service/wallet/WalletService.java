package com.bituan.payjor.service.wallet;

import com.bituan.payjor.model.request.WalletTransferRequest;
import com.bituan.payjor.model.response.wallet.WalletBalanceResponse;
import com.bituan.payjor.model.response.wallet.WalletTransactionResponse;
import com.bituan.payjor.model.response.wallet.WalletTransferResponse;

import java.util.List;

public interface WalletService {
    WalletTransferResponse transfer(WalletTransferRequest request);
    WalletBalanceResponse getBalance();
    List<WalletTransactionResponse> getAllTransactions();
}
