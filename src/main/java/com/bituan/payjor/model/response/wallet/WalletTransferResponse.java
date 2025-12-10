package com.bituan.payjor.model.response.wallet;

import com.bituan.payjor.model.enums.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class WalletTransferResponse {
    private TransactionStatus status;
    private String message;
}
