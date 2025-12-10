package com.bituan.payjor.controller;

import com.bituan.payjor.model.request.WalletTransferRequest;
import com.bituan.payjor.model.response.ApiResponse;
import com.bituan.payjor.model.response.wallet.WalletBalanceResponse;
import com.bituan.payjor.model.response.wallet.WalletTransferResponse;
import com.bituan.payjor.service.wallet.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wallet")
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<?>> transfer (WalletTransferRequest request) {

        ApiResponse<WalletTransferResponse> response = new ApiResponse<>(HttpStatus.OK.value(), walletService.transfer(request));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/balance")
    public ResponseEntity<ApiResponse<?>> getBalance () {

        ApiResponse<WalletBalanceResponse> response = new ApiResponse<>(HttpStatus.OK.value(), walletService.getBalance());
        return ResponseEntity.ok(response);
    }
}
