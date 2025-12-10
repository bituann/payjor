package com.bituan.payjor.model.entity;

import com.bituan.payjor.model.enums.TransactionStatus;
import com.bituan.payjor.model.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "transactions")
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", columnDefinition = "UUID")
    private User user;

    @ManyToOne
    @JoinColumn(name = "wallet_id", columnDefinition = "UUID")
    private Wallet wallet;

    @ManyToMany
    @JoinColumn(name = "recipient_id", columnDefinition = "UUID")
    private User recipient;

    @Column(nullable = false)
    private double amount;

    private TransactionType type;

    private TransactionStatus status;
}
