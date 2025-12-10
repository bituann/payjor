package com.bituan.payjor.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@Builder
@Table(name = "wallets")
@AllArgsConstructor
@NoArgsConstructor
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @Column(scale = 2)
    private double balance;

    @Column(unique = true, length = 13)
    private String number;

    @OneToOne
    @JoinColumn(name = "owner_id", columnDefinition = "UUID", referencedColumnName = "id")
    private User owner;
}
