package com.bituan.payjor.service.auth;

import com.bituan.payjor.model.response.auth.AuthResponse;

public interface AuthService {
    String initiateGoogleOAuth();
    AuthResponse signIn(String code);
}
