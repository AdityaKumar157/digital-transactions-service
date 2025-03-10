package com.makeprojects.digitaltransactionsservice.transactions.api.controller;

import com.makeprojects.digitaltransactionsservice.transactions.core.service.definition.TransactionService;
import com.makeprojects.digitaltransactionsservice.transactions.database.model.Transaction;
import com.makeprojects.digitaltransactionsservice.transactions.dto.TransactionRequestDTO;
import com.makeprojects.ewallet.shared.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/txn")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<Transaction> checkStatus(@PathVariable UUID transactionId) {
        try {
            Transaction transaction = this.transactionService.get(transactionId);
            return ResponseEntity.ok(transaction);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/initiate")
    public ResponseEntity<Transaction> startTransaction(@RequestBody TransactionRequestDTO transactionRequestDTO) {
        try {
            Transaction transaction = this.transactionService.initiateTransaction(transactionRequestDTO);
            return ResponseEntity.ok(transaction);
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Tested!!");
    }
}
