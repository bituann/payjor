package com.bituan.payjor.service.wallet;

import com.bituan.payjor.model.request.WalletTransferRequest;
import com.bituan.payjor.model.response.wallet.WalletTransferResponse;

public interface WalletService {
    WalletTransferResponse transfer(WalletTransferRequest request);
}
