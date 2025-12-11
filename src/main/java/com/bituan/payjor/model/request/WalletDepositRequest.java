package com.bituan.payjor.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WalletDepositRequest {
    private int amount;
}
