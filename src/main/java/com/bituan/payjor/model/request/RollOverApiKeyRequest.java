package com.bituan.payjor.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class RollOverApiKeyRequest {
    private UUID expiredKeyId;
    private String expiry;
}
