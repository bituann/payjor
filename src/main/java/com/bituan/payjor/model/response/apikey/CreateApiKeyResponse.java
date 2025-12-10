package com.bituan.payjor.model.response.apikey;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class CreateApiKeyResponse {
    private String apiKey;
    private LocalDateTime expiresAt;
}
