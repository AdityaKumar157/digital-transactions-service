package com.makeprojects.digitaltransactionsservice.transactions.database.repository;

import com.makeprojects.digitaltransactionsservice.transactions.database.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
}
