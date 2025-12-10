package com.bituan.payjor.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletTransferRequest {
    private String walletNumber;
    private double amount;
}
