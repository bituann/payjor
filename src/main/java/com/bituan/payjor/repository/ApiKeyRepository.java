package com.bituan.payjor.repository;

import com.bituan.payjor.model.entity.ApiKey;
import com.bituan.payjor.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {
    Optional<ApiKey> findByKey(String key);
    List<ApiKey> findAllByOwner(User user);
}
