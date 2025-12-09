package com.bituan.payjor.model.entity;

import com.bituan.payjor.model.enums.TransactionStatus;
import com.bituan.payjor.model.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UUID user;

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    @ManyToMany
    @JoinColumn(name = "recipient_id")
    private User recipient;

    private TransactionType type;

    private TransactionStatus status;
}
