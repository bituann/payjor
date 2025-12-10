package com.bituan.payjor.service;

import com.bituan.payjor.model.entity.User;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class TokenServiceImpl implements TokenService{

    private final JwtEncoder jwtEncoder;

    public TokenServiceImpl(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    @Override
    public String generateJwtToken(User user) {
        Instant now = Instant.now();
        Instant expiryDate = now.plus(1, ChronoUnit.HOURS);

        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .subject(user.getEmail())
                .issuedAt(now)
                .expiresAt(expiryDate)
                .claim("userGoogleId", user.getGoogleUserId())
                .claim("fullName", user.getFullName())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();
    }
}
