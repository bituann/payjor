package com.bituan.payjor.model.response.wallet;

import com.bituan.payjor.model.enums.TransactionStatus;
import com.bituan.payjor.model.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class WalletTransactionResponse {
    private TransactionType type;
    private double amount;
    private TransactionStatus status;
}
