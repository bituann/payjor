package com.bituan.payjor.service;

import com.bituan.payjor.model.entity.User;
import com.bituan.payjor.model.request.CreateApiKeyRequest;
import com.bituan.payjor.model.response.apikey.CreateApiKeyResponse;

public interface TokenService {
    String generateJwtToken(User user);
    CreateApiKeyResponse createApiKey(CreateApiKeyRequest request);
}
