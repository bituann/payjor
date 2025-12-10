package com.bituan.payjor.model.request.paystack;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InitPaymentRequest {
    private int amount;       // in kobo
    private String email;
    private String reference;
}
