package com.bituan.payjor.model.request.paystack;

import lombok.Data;

@Data
public class InitPaymentRequest {
    private int amount;       // in kobo
    private String email;
    private String reference;
}
