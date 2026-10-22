package com.example.fraudring.controller;

import com.example.fraudring.model.Transaction;
import com.example.fraudring.service.TransactionService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(
            TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // Get all transactions
    @GetMapping
    public List<Transaction> getTransactions() {
        return transactionService.getAllTransactions();
    }

    // Add a new transaction
    @PostMapping
    public ResponseEntity<?> addTransaction(
            @RequestBody Transaction transaction) {

        try {
            Transaction savedTransaction =
                    transactionService.addTransaction(transaction);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(savedTransaction);

        } catch (IllegalArgumentException exception) {

            return ResponseEntity
                    .badRequest()
                    .body(exception.getMessage());
        }
    }
}