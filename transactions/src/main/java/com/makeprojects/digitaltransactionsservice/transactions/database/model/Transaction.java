package com.makeprojects.digitaltransactionsservice.transactions.database.model;

import com.makeprojects.digitaltransactionsservice.transactions.core.enums.TransactionStatus;
import com.makeprojects.ewallet.shared.core.enums.transaction.TransactionEnums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Builder
@With
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID transactionId;

    private UUID accountId;     // sender account
    private UUID targetAccountId;       // receiver account

    private double amount;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;    // WALLET_TO_WALLET, WALLET_TO_BANK, BANK_TO_WALLET

    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;    // PENDING, COMPLETED, FAILED

    private Instant createdAt;
    private Instant updatedAt;
}
