package com.bituan.payjor.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @GetMapping("/google")
    public void initiateGoogleSignIn(HttpServletResponse response) {
        try {
            response.sendRedirect("/oauth2/authorization/google");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/google/callback")
    public String completeSignIn(OAuth2AuthenticationToken token) {
        return "Hello " + token.getPrincipal();
    }
}
