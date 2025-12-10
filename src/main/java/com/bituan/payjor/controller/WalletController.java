package com.bituan.payjor.controller;

import com.bituan.payjor.model.request.WalletTransferRequest;
import com.bituan.payjor.model.response.ApiResponse;
import com.bituan.payjor.model.response.wallet.WalletBalanceResponse;
import com.bituan.payjor.model.response.wallet.WalletTransactionResponse;
import com.bituan.payjor.model.response.wallet.WalletTransferResponse;
import com.bituan.payjor.service.wallet.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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

    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<?>> getAllTransactions() {

        ApiResponse<List<WalletTransactionResponse>> response = new ApiResponse<>(HttpStatus.OK.value(), walletService.getAllTransactions());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/deposit/{reference}/status")
    public ResponseEntity<ApiResponse<?>> getTransactionStatus(@PathVariable UUID reference) {

        ApiResponse<WalletTransactionResponse> response = new ApiResponse<>(HttpStatus.OK.value(), walletService.getTransactionStatus(reference));
        return ResponseEntity.ok(response);
    }
}
