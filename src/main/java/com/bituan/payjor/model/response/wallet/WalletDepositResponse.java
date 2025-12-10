package com.bituan.payjor.model.response.wallet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WalletDepositResponse {
    private String reference;
    private String authorizationUrl;
}
