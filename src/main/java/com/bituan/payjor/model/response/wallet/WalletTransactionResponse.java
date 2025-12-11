package com.bituan.payjor.model.response.wallet;

import com.bituan.payjor.model.enums.TransactionStatus;
import com.bituan.payjor.model.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WalletTransactionResponse {
    private String reference;
    private TransactionType type;
    private double amount;
    private String sender;
    private String recipient;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}
