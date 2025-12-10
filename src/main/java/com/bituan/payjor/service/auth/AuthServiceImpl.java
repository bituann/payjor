package com.bituan.payjor.service.auth;

import com.bituan.payjor.model.entity.User;
import com.bituan.payjor.model.response.auth.AuthResponse;
import com.bituan.payjor.repository.UserRepository;
import com.bituan.payjor.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final TokenService tokenService;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository, TokenService tokenService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    @Override
    public AuthResponse signIn(OidcUser user) {
        // check if user exists in db
        User dbUser = userRepository.findByEmail(user.getEmail()).orElse(User.builder()
                .googleUserId(user.getSubject())
                .email(user.getEmail())
                .fullName(user.getFullName())
                // create & attach wallet
                .build());

        if (dbUser.getId() == null) {
            userRepository.save(dbUser);
        }

        String token = tokenService.generateJwtToken(dbUser);

        return AuthResponse.builder()
                .token(token)
                .build();
    }
}
