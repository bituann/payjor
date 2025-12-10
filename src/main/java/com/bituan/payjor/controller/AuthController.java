package com.bituan.payjor.controller;

import com.bituan.payjor.model.response.ApiResponse;
import com.bituan.payjor.model.response.auth.AuthResponse;
import com.bituan.payjor.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final ClientRegistrationRepository clientRegistrationRepository;

    @GetMapping("/google")
    @Operation(
            summary = "Initiate Google Signin"
    )
    public ResponseEntity<ApiResponse<?>> initiateGoogleSignIn(HttpServletResponse response) {

        ApiResponse<Map<String, String>> res = new ApiResponse<>(HttpStatus.OK.value(), Map.of("authorization_url", authService.initiateGoogleOAuth()));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/google/callback")
    @Operation(
            summary = "Callback to authenticates the user"
    )
    public ResponseEntity<ApiResponse<AuthResponse>> completeSignIn(@RequestParam("code") String code) {

        ApiResponse<AuthResponse> response = new ApiResponse<>(HttpStatus.OK.value(), authService.signIn(code));

        return ResponseEntity.ok(response);
    }
}
