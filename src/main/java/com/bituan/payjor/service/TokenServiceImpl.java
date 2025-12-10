package com.bituan.payjor.service;

import com.bituan.payjor.model.entity.ApiKey;
import com.bituan.payjor.model.entity.User;
import com.bituan.payjor.model.request.CreateApiKeyRequest;
import com.bituan.payjor.model.request.RollOverApiKeyRequest;
import com.bituan.payjor.model.response.apikey.CreateApiKeyResponse;
import com.bituan.payjor.repository.ApiKeyRepository;
import com.bituan.payjor.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService{

    private final JwtEncoder jwtEncoder;
    private final PasswordEncoder passwordEncoder;
    private final ApiKeyRepository apiKeyRepository;

    @Override
    public String generateJwtToken(User user) {
        Instant now = Instant.now();
        Instant expiryDate = now.plus(1, ChronoUnit.HOURS);

        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .subject(user.getEmail())
                .issuedAt(now)
                .expiresAt(expiryDate)
                .claim("email", user.getEmail())
                .claim("fullName", user.getFullName())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();
    }

    @Override
    public CreateApiKeyResponse createApiKey(CreateApiKeyRequest request) {
        User user = UserService.getAuthenticatedUser();

        if (user.getActiveKeys() > 5) {
            throw new RuntimeException("A person can only own a maximum of 5 keys");
        }

        if (request.getPermissions().isEmpty()) {
            throw new RuntimeException("Permissions must be set");
        }

        // Generate random 32-byte key
        byte[] randomBytes = new byte[32];
        new SecureRandom().nextBytes(randomBytes);
        String rawKey = "sk_live_" + Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        // Hash before storing
        String hashedKey = passwordEncoder.encode(rawKey);

        // calculate expired at
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime expiresAt = createdAt;

        expiresAt = getExpiryDate(request.getExpiry());

        ApiKey apiKey = ApiKey.builder()
                .name(request.getName())
                .key(hashedKey)
                .owner(user)
                .permissions(request.getPermissions())
                .createdAt(createdAt)
                .expiresAt(expiresAt)
                .build();

        apiKeyRepository.save(apiKey);
        
        user.setActiveKeys(user.getActiveKeys() + 1);

        // Return plaintext key to user (store only hashed version in DB)
        return CreateApiKeyResponse.builder()
                .apiKey(rawKey)
                .expiresAt(expiresAt)
                .build();
    }

    @Override
    public CreateApiKeyResponse rollOverApiKey(RollOverApiKeyRequest request) {
        ApiKey apiKey = apiKeyRepository.findById(request.getExpiredKeyId()).orElseThrow(RuntimeException::new);

        if (apiKey.getExpiresAt().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("You cannot rollover this key");
        }

        CreateApiKeyRequest req = CreateApiKeyRequest.builder()
                .name(apiKey.getName())
                .permissions(apiKey.getPermissions())
                .expiry(request.getExpiry())
                .build();

        return createApiKey(req);
    }

    private LocalDateTime getExpiryDate(String expiry) {
        LocalDateTime now = LocalDateTime.now();

        return switch (expiry) {
            case "1D" -> now.plusDays(1);
            case "1W" -> now.plusWeeks(1);
            case "1M" -> now.plusMonths(1);
            case "1Y" -> now.plusYears(1);
            default -> now.plusSeconds(0);
        };
    }


}
