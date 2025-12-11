package com.bituan.payjor.model.response.wallet;

import com.bituan.payjor.model.enums.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class WalletTransferResponse {
    private long accountNumber;
    private long recipient;
    private double amount;
    private TransactionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}
