package com.bituan.payjor.service;

import com.bituan.payjor.model.entity.User;

public interface TokenService {
    String generateJwtToken(User user);
}
