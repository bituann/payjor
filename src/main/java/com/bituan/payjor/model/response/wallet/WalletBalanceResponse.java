package com.bituan.payjor.model.response.wallet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class WalletBalanceResponse {
    private String accountNumber;
    private double balance;
}
