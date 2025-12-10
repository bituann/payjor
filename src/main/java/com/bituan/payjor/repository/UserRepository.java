package com.bituan.payjor.repository;

import com.bituan.payjor.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByGoogleUserId(String googleUserId);
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
}
