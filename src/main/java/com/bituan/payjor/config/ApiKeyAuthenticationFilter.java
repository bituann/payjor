package com.bituan.payjor.config;

import com.bituan.payjor.model.entity.ApiKey;
import com.bituan.payjor.model.entity.CustomUserDetails;
import com.bituan.payjor.repository.ApiKeyRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private final ApiKeyRepository apiKeyRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String apiKeyHeader = request.getHeader("X-API-KEY");

        if(apiKeyHeader == null) {
            filterChain.doFilter(request, response);
            return;
        }

        Optional<ApiKey> matchedKey = apiKeyRepository.findAll().stream()
                .filter(k -> passwordEncoder.matches(apiKeyHeader, k.getKey()))
                .findFirst();


        if (matchedKey.isPresent()) {
            ApiKey apiKey = matchedKey.get();

            CustomUserDetails userDetails = new CustomUserDetails(apiKey.getOwner());

            // Build Authentication object
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            apiKey.getPermissions().stream()
                                    .map(permission -> new SimpleGrantedAuthority("ROLE_" + permission.name()))
                                    .toList()
                    );

            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}

