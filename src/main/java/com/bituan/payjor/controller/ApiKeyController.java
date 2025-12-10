package com.bituan.payjor.controller;

import com.bituan.payjor.model.request.CreateApiKeyRequest;
import com.bituan.payjor.model.response.ApiResponse;
import com.bituan.payjor.model.response.apikey.CreateApiKeyResponse;
import com.bituan.payjor.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/keys")
@RequiredArgsConstructor
public class ApiKeyController {
    private final TokenService tokenService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<?>> createApiKey (@RequestBody CreateApiKeyRequest request) {

        ApiResponse<CreateApiKeyResponse> res = new ApiResponse<>(HttpStatus.OK.value(), tokenService.createApiKey(request));
        return ResponseEntity.ok(res);
    }
}
