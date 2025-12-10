package com.bituan.payjor.service.auth;

import com.bituan.payjor.model.response.auth.AuthResponse;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public interface AuthService {
    AuthResponse signIn(OidcUser user);
}
