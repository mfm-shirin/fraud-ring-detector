
package com.example.fraudring.service;

import com.example.fraudring.model.Transaction;
import com.example.fraudring.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
        seedSampleTransactionsIfEmpty();
    }

    @Transactional
    public void seedSampleTransactionsIfEmpty() {
        if (transactionRepository.count() > 0) {
            return;
        }

        Instant now = Instant.now();

        transactionRepository.saveAll(List.of(
            new Transaction("TXN001", "ACCOUNT_A", "ACCOUNT_B",
                    1500.0, now.minusSeconds(50)),
            new Transaction("TXN002", "ACCOUNT_C", "ACCOUNT_B",
                    2000.0, now.minusSeconds(45)),
            new Transaction("TXN003", "ACCOUNT_D", "ACCOUNT_B",
                    2500.0, now.minusSeconds(40)),
            new Transaction("TXN004", "ACCOUNT_E", "ACCOUNT_B",
                    1800.0, now.minusSeconds(30)),
            new Transaction("TXN005", "ACCOUNT_F", "ACCOUNT_B",
                    3000.0, now.minusSeconds(20)),
            new Transaction("TXN006", "ACCOUNT_B", "ACCOUNT_G",
                    8500.0, now.minusSeconds(10)),
            new Transaction("TXN007", "ACCOUNT_H", "ACCOUNT_I",
                    75000.0, now.minusSeconds(5)),
            new Transaction("TXN008", "ACCOUNT_J", "ACCOUNT_K",
                    500.0, now.minusSeconds(9)),
            new Transaction("TXN009", "ACCOUNT_J", "ACCOUNT_L",
                    600.0, now.minusSeconds(7)),
            new Transaction("TXN010", "ACCOUNT_J", "ACCOUNT_M",
                    700.0, now.minusSeconds(5)),
            new Transaction("TXN011", "ACCOUNT_J", "ACCOUNT_N",
                    800.0, now.minusSeconds(3)),
            new Transaction("TXN012", "ACCOUNT_J", "ACCOUNT_O",
                    900.0, now.minusSeconds(1)),
            new Transaction("TXN013", "ACCOUNT_P", "ACCOUNT_Q",
                    10000.0, now.minusSeconds(15)),
            new Transaction("TXN014", "ACCOUNT_Q", "ACCOUNT_R",
                    9800.0, now.minusSeconds(10)),
            new Transaction("TXN015", "ACCOUNT_R", "ACCOUNT_P",
                    9500.0, now.minusSeconds(5))
        ));
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @Transactional
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

        if (transactionRepository
                .existsByTransactionIdIgnoreCase(
                        transaction.getTransactionId())) {
            throw new IllegalArgumentException(
                    "Transaction ID already exists.");
        }

        if (transaction.getTimestamp() == null) {
            transaction.setTimestamp(Instant.now());
        }

        return transactionRepository.save(transaction);
    }
}
