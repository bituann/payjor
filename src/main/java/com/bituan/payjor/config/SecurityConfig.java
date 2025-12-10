package com.bituan.payjor.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import io.jsonwebtoken.security.Jwk;
import io.jsonwebtoken.security.Jwks;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final RsaKeyProperties rsaKeys;

    public SecurityConfig(RsaKeyProperties rsaKeys) {
        this.rsaKeys = rsaKeys;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/", "/auth/google", "/oauth2/**", "/login/oauth2/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, authException) -> {
                            // prevent Spring from forcing Google login automatically
                            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        })
                )
                .oauth2Login(oauth -> oauth
                        .loginPage("/auth/google")
                        .defaultSuccessUrl("/auth/google/callback", true)
                )
//                .addFilterAfter(bearerTokenAuthFilter, BasicAuthenticationFilter.class);
                .build();
    }

    @Bean
    public JwtDecoder jwtDecoder () {
        try {
            RSAPublicKey publicKey = loadPublicKey(rsaKeys.publicKey());
            return NimbusJwtDecoder.withPublicKey(publicKey).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        try {
            RSAPublicKey publicKey = loadPublicKey(rsaKeys.publicKey());
            RSAPrivateKey privateKey = loadPrivateKey(rsaKeys.privateKey());

            JWK jwk = new RSAKey.Builder(publicKey).privateKey(privateKey).build();
            JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
            return new NimbusJwtEncoder(jwks);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private RSAPrivateKey loadPrivateKey(String pem) throws Exception {
        String content = pem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] decoded = Base64.getDecoder().decode(content);
        return (RSAPrivateKey) KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(decoded));
    }

    private RSAPublicKey loadPublicKey(String pem) throws Exception {
        String content = pem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] decoded = Base64.getDecoder().decode(content);
        return (RSAPublicKey) KeyFactory.getInstance("RSA")
                .generatePublic(new PKCS8EncodedKeySpec(decoded));
    }


}
