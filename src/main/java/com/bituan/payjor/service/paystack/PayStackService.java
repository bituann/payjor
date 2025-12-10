package com.bituan.payjor.service.paystack;

import com.bituan.payjor.model.request.paystack.InitPaymentRequest;
import com.bituan.payjor.model.response.paystack.InitPaymentResponse;
import com.bituan.payjor.model.response.paystack.VerifyPaymentResponse;

public interface PayStackService {
    InitPaymentResponse initializePayment(InitPaymentRequest request);
    VerifyPaymentResponse verifyPayment(String reference);
}
