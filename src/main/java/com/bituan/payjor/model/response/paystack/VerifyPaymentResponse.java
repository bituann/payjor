package com.bituan.payjor.model.response.paystack;

import lombok.Data;

@Data
public class VerifyPaymentResponse {
    private boolean status;
    private String message;
    private VerifyData data;

    @Data
    public static class VerifyData {
        private String reference;
        private String status;  // success, failed, abandoned
        private int amount;
        private String gateway_response;
    }
}

