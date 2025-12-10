package com.bituan.payjor.model.response.paystack;

import lombok.Data;

@Data
public class InitPaymentResponse {
    private boolean status;
    private String message;
    private InitPaymentData data;

    @Data
    public static class InitPaymentData {
        private String authorization_url;
        private String access_code;
        private String reference;
    }
}

