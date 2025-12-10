package com.bituan.payjor.repository;

import com.bituan.payjor.model.entity.Transaction;
import com.bituan.payjor.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findBySenderOrRecipient(User userId, User recipientId);
    Optional<Transaction> findByReference(String reference);
}
