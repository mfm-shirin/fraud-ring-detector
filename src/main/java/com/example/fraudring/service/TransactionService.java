package com.example.fraudring.service;

import com.example.fraudring.model.Transaction;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionService {

    private final List<Transaction> transactions = new ArrayList<>();

    public TransactionService() {
        Instant now = Instant.now();

        // Sample transactions for rapid inflow and outflow detection
        transactions.add(new Transaction(
                "TXN001", "ACCOUNT_A", "ACCOUNT_B", 1500.0,
                now.minusSeconds(50)));

        transactions.add(new Transaction(
                "TXN002", "ACCOUNT_C", "ACCOUNT_B", 2000.0,
                now.minusSeconds(45)));

        transactions.add(new Transaction(
                "TXN003", "ACCOUNT_D", "ACCOUNT_B", 2500.0,
                now.minusSeconds(40)));

        transactions.add(new Transaction(
                "TXN004", "ACCOUNT_E", "ACCOUNT_B", 1800.0,
                now.minusSeconds(30)));

        transactions.add(new Transaction(
                "TXN005", "ACCOUNT_F", "ACCOUNT_B", 3000.0,
                now.minusSeconds(20)));

        transactions.add(new Transaction(
                "TXN006", "ACCOUNT_B", "ACCOUNT_G", 8500.0,
                now.minusSeconds(10)));

        // Sample transaction for large transaction detection
        transactions.add(new Transaction(
                "TXN007", "ACCOUNT_H", "ACCOUNT_I", 75000.0,
                now.minusSeconds(5)));

        // Sample transactions for repeated rapid transaction detection
        transactions.add(new Transaction(
                "TXN008", "ACCOUNT_J", "ACCOUNT_K", 500.0,
                now.minusSeconds(9)));

        transactions.add(new Transaction(
                "TXN009", "ACCOUNT_J", "ACCOUNT_L", 600.0,
                now.minusSeconds(7)));

        transactions.add(new Transaction(
                "TXN010", "ACCOUNT_J", "ACCOUNT_M", 700.0,
                now.minusSeconds(5)));

        transactions.add(new Transaction(
                "TXN011", "ACCOUNT_J", "ACCOUNT_N", 800.0,
                now.minusSeconds(3)));

        transactions.add(new Transaction(
                "TXN012", "ACCOUNT_J", "ACCOUNT_O", 900.0,
                now.minusSeconds(1)));

        // Sample transactions for circular transfer detection
        transactions.add(new Transaction(
                "TXN013", "ACCOUNT_P", "ACCOUNT_Q", 10000.0,
                now.minusSeconds(15)));

        transactions.add(new Transaction(
                "TXN014", "ACCOUNT_Q", "ACCOUNT_R", 9800.0,
                now.minusSeconds(10)));

        transactions.add(new Transaction(
                "TXN015", "ACCOUNT_R", "ACCOUNT_P", 9500.0,
                now.minusSeconds(5)));
    }

    // Retrieve all transactions
    public List<Transaction> getAllTransactions() {
        return transactions;
    }

    // Validate and add a new transaction
    public Transaction addTransaction(Transaction transaction) {

        if (transaction.getTransactionId() == null
                || transaction.getTransactionId().isBlank()) {
            throw new IllegalArgumentException(
                    "Transaction ID is required.");
        }

        if (transaction.getFromAccount() == null
                || transaction.getFromAccount().isBlank()) {
            throw new IllegalArgumentException(
                    "Sender account is required.");
        }

        if (transaction.getToAccount() == null
                || transaction.getToAccount().isBlank()) {
            throw new IllegalArgumentException(
                    "Receiver account is required.");
        }

        if (transaction.getFromAccount().equalsIgnoreCase(
                transaction.getToAccount())) {
            throw new IllegalArgumentException(
                    "Sender and receiver accounts must be different.");
        }

        if (!Double.isFinite(transaction.getAmount())
                || transaction.getAmount() <= 0) {
            throw new IllegalArgumentException(
                    "Transaction amount must be greater than zero.");
        }

        boolean duplicateId = transactions.stream()
                .anyMatch(existing ->
                        existing.getTransactionId().equalsIgnoreCase(
                                transaction.getTransactionId()));

        if (duplicateId) {
            throw new IllegalArgumentException(
                    "Transaction ID already exists.");
        }

        if (transaction.getTimestamp() == null) {
            transaction.setTimestamp(Instant.now());
        }

        transactions.add(transaction);

        return transaction;
    }
}