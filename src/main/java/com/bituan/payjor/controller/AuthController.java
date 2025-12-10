package com.bituan.payjor.controller;

import com.bituan.payjor.model.response.ApiResponse;
import com.bituan.payjor.model.response.auth.AuthResponse;
import com.bituan.payjor.service.auth.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/google")
    public void initiateGoogleSignIn(HttpServletResponse response) {
        try {
            response.sendRedirect("/oauth2/authorization/google");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/google/callback")
    public ResponseEntity<ApiResponse<AuthResponse>> completeSignIn(@AuthenticationPrincipal OidcUser user) {

        ApiResponse<AuthResponse> response = new ApiResponse<>(HttpStatus.OK.value(), authService.signIn(user));

        return ResponseEntity.ok(response);
    }
}
