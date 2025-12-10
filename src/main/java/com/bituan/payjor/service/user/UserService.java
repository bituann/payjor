package com.bituan.payjor.service.user;

import com.bituan.payjor.model.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserService {

    public static User getAuthenticatedUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found");
        }

        return (User) authentication.getPrincipal();
    }
}
