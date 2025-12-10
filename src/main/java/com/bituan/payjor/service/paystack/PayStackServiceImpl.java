package com.bituan.payjor.service.paystack;

import com.bituan.payjor.model.request.paystack.InitPaymentRequest;
import com.bituan.payjor.model.response.paystack.InitPaymentResponse;
import com.bituan.payjor.model.response.paystack.VerifyPaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class PayStackServiceImpl implements PayStackService{

    private final WebClient payStackClient;

    @Override
    public InitPaymentResponse initializePayment(InitPaymentRequest request) {
        return payStackClient.post()
                .uri("/transaction/initialize")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(InitPaymentResponse.class)
                .block();
    }

    @Override
    public VerifyPaymentResponse verifyPayment(String reference) {
        return payStackClient.get()
                .uri("/transaction/verify/{reference}", reference)
                .retrieve()
                .bodyToMono(VerifyPaymentResponse.class)
                .block();
    }
}
