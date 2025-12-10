package com.bituan.payjor.controller;

import com.bituan.payjor.model.request.WalletTransferRequest;
import com.bituan.payjor.model.response.ApiResponse;
import com.bituan.payjor.model.response.wallet.WalletBalanceResponse;
import com.bituan.payjor.model.response.wallet.WalletDepositResponse;
import com.bituan.payjor.model.response.wallet.WalletTransactionResponse;
import com.bituan.payjor.model.response.wallet.WalletTransferResponse;
import com.bituan.payjor.service.wallet.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/wallet")
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @PostMapping("/transfer")
    @Operation(
            summary = "Transfer money between users of the app"
    )
    public ResponseEntity<ApiResponse<?>> transfer (WalletTransferRequest request) {

        ApiResponse<WalletTransferResponse> response = new ApiResponse<>(HttpStatus.OK.value(), walletService.transfer(request));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/balance")
    @Operation(
            summary = "Get user balance"
    )
    public ResponseEntity<ApiResponse<?>> getBalance () {

        ApiResponse<WalletBalanceResponse> response = new ApiResponse<>(HttpStatus.OK.value(), walletService.getBalance());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/transactions")
    @Operation(
            summary = "Gets all user transactions"
    )
    public ResponseEntity<ApiResponse<?>> getAllTransactions() {

        ApiResponse<List<WalletTransactionResponse>> response = new ApiResponse<>(HttpStatus.OK.value(), walletService.getAllTransactions());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/deposit/{reference}/status")
    @Operation(
            summary = "Gets the status of a deposit"
    )
    public ResponseEntity<ApiResponse<?>> getTransactionStatus(@PathVariable UUID reference) {

        ApiResponse<WalletTransactionResponse> response = new ApiResponse<>(HttpStatus.OK.value(), walletService.getTransactionStatus(reference));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<?>> deposit (@RequestBody int amount) {

        ApiResponse<WalletDepositResponse> res = new ApiResponse<>(HttpStatus.OK.value(), walletService.deposit(amount));

        return ResponseEntity.ok(res);
    }

    @PostMapping("/paystack/webhook")
    public ResponseEntity<ApiResponse<?>> handleWebHook (@RequestHeader("X-Paystack-Signature") String signature, @RequestBody String payload) {

        ApiResponse<Map<String, Boolean>> res = new ApiResponse<>(HttpStatus.OK.value(), Map.of("status", walletService.handleWebHook(signature, payload)));

        return ResponseEntity.ok(res);
    }
}
