package com.bituan.payjor.service.auth;

import com.bituan.payjor.exception.BadRequestException;
import com.bituan.payjor.model.entity.User;
import com.bituan.payjor.model.entity.Wallet;
import com.bituan.payjor.model.response.auth.AuthResponse;
import com.bituan.payjor.repository.UserRepository;
import com.bituan.payjor.repository.WalletRepository;
import com.bituan.payjor.service.TokenService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final NetHttpTransport TRANSPORT = new NetHttpTransport();
    private static final GsonFactory JSON_FACTORY = new GsonFactory();

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final WebClient webClient = WebClient.create();
    private final WalletRepository walletRepository;

    @Value("${GOOGLE_CLIENT_ID}")
    private String googleClientId;

    @Override
    public String initiateGoogleOAuth() {
        ClientRegistration googleClientRegistration = clientRegistrationRepository.findByRegistrationId("google");

        // Construct the authorization URL

        return UriComponentsBuilder.fromUriString(googleClientRegistration.getProviderDetails().getAuthorizationUri())
                .queryParam("client_id", googleClientRegistration.getClientId())
                .queryParam("redirect_uri", googleClientRegistration.getRedirectUri())
                .queryParam("response_type", "code")
                .queryParam("scope", String.join(" ", googleClientRegistration.getScopes()))
                .queryParam("access_type", "offline")
                .queryParam("prompt", "consent")
                .build().toUriString();
    }

    @Override
    public AuthResponse signIn(String code) {
        // Exchange code for token
        ClientRegistration registration = clientRegistrationRepository.findByRegistrationId("google");

        // Exchange code for tokens
        Map<String, String> tokenResponse = webClient.post()
                .uri(registration.getProviderDetails().getTokenUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("code", code)
                        .with("client_id", registration.getClientId())
                        .with("client_secret", registration.getClientSecret())
                        .with("redirect_uri", registration.getRedirectUri())
                        .with("grant_type", "authorization_code"))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {})
                .block();

        if (tokenResponse == null) {
            throw new BadRequestException("The provided code may be invalid");
        }

        String idTokenString = tokenResponse.get("id_token");   // JWT signed by Google
        String accessToken = tokenResponse.get("access_token");

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(TRANSPORT, JSON_FACTORY)
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken idToken;
        try {
            idToken = verifier.verify(idTokenString);
        } catch (Exception e) {
            throw new BadRequestException("Invalid Google token");
        }

        if (idToken == null) {
            throw new BadRequestException("Invalid Google token");
        }

        GoogleIdToken.Payload payload = idToken.getPayload();

        String googleId = payload.getSubject();

        // Save or update user in DB
        User user = userRepository.findByGoogleUserId(googleId)
                .orElse(createNewAccount(payload));

        userRepository.save(user);

        // Generate JWT
        String token = tokenService.generateJwtToken(user);

        return AuthResponse.builder()
                .token(token)
                .build();
    }

    private User createNewAccount (GoogleIdToken.Payload payload) {
        User user = User.builder()
                .googleUserId(payload.getSubject())
                .email(payload.getEmail())
                .fullName((String) payload.get("name"))
                .activeKeys(0)
                .wallet(
                        Wallet.builder()
                                .balance(0)
                                .number(generateAccountNumber())
                                .build()
                )
                .build();
        user.getWallet().setOwner(user);

        return user;
    }

    private long generateAccountNumber() {
        long number = (long) (Math.random() * (9_999_999_999_999L - 1_000_000_000_000L + 1) + 1_000_000_000_000L);

        while (!walletRepository.existsByNumber(number)) {
            number = (long) (Math.random() * (9_999_999_999_999L - 1_000_000_000_000L + 1) + 1_000_000_000_000L);
        }

        return number;
    }
}
