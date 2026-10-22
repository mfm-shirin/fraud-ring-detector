package com.example.fraudring.service;

import com.example.fraudring.model.FraudAlert;
import com.example.fraudring.model.Transaction;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class FraudDetectionService {

    private static final int MIN_DISTINCT_SENDERS = 5;
    private static final long WINDOW_SECONDS = 60;
    private static final double LARGE_TRANSACTION_THRESHOLD = 50000.0;
    private static final int RAPID_TRANSACTION_LIMIT = 5;

    private final TransactionService transactionService;

    public FraudDetectionService(
            TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public List<FraudAlert> detectFraud() {

        List<Transaction> transactions =
                transactionService.getAllTransactions();

        List<FraudAlert> alerts = new ArrayList<>();

        // Rule 1: Detect rapid inflow followed by an outgoing transfer.
        for (Transaction outgoing : transactions) {

            String accountId = outgoing.getFromAccount();
            Instant outgoingTime = outgoing.getTimestamp();

            Set<String> distinctSenders = new HashSet<>();

            for (Transaction incoming : transactions) {

                if (!incoming.getToAccount().equals(accountId)) {
                    continue;
                }

                if (incoming.getTimestamp().isAfter(outgoingTime)) {
                    continue;
                }

                long secondsBetween = Duration.between(
                        incoming.getTimestamp(),
                        outgoingTime
                ).getSeconds();

                if (secondsBetween >= 0
                        && secondsBetween <= WINDOW_SECONDS) {
                    distinctSenders.add(incoming.getFromAccount());
                }
            }

            if (distinctSenders.size() >= MIN_DISTINCT_SENDERS) {

                FraudAlert alert = new FraudAlert(
                        accountId,
                        "RAPID_INFLOW_OUTFLOW",
                        "Account received money from "
                                + distinctSenders.size()
                                + " distinct senders within 60 seconds "
                                + "before making an outgoing transfer.",
                        distinctSenders.size(),
                        outgoing.getAmount(),
                        outgoingTime,
                        "HIGH"
                );

                alerts.add(alert);
            }
        }

        // Rule 2: Detect unusually large transactions.
        for (Transaction transaction : transactions) {

            if (transaction.getAmount() > LARGE_TRANSACTION_THRESHOLD) {

                FraudAlert alert = new FraudAlert(
                        transaction.getFromAccount(),
                        "LARGE_TRANSACTION",
                        "Transaction amount exceeds the configured threshold of 50000.",
                        0,
                        transaction.getAmount(),
                        transaction.getTimestamp(),
                        "HIGH"
                );

                alerts.add(alert);
            }
        }
        
        
        // Rule 3: Detect repeated rapid transactions using a sliding window.
        Set<String> alertedAccounts = new HashSet<>();

        for (Transaction transaction : transactions) {

        String accountId = transaction.getFromAccount();

        if (alertedAccounts.contains(accountId)) {
                continue;
        }

        Instant windowEnd = transaction.getTimestamp();
        Instant windowStart = windowEnd.minusSeconds(10);

        int transactionCount = 0;

        for (Transaction other : transactions) {

                if (!other.getFromAccount().equals(accountId)) {
                continue;
                }

                Instant otherTime = other.getTimestamp();

                // Count transactions inside this 10-second window.
                if (!otherTime.isBefore(windowStart)
                        && !otherTime.isAfter(windowEnd)) {
                transactionCount++;
                }
        }

        if (transactionCount >= RAPID_TRANSACTION_LIMIT) {

                FraudAlert alert = new FraudAlert(
                        accountId,
                        "REPEATED_RAPID_TRANSACTIONS",
                        "Account initiated "
                                + transactionCount
                                + " transactions within a 10-second window.",
                        0,
                        transaction.getAmount(),
                        windowEnd,
                        "HIGH"
                );

                alerts.add(alert);
                alertedAccounts.add(accountId);
        }
        }
        
        // Rule 4: Detect circular transfers involving three accounts.
        for (Transaction first : transactions) {

        for (Transaction second : transactions) {

                if (!second.getFromAccount().equals(first.getToAccount())) {
                continue;
                }

                for (Transaction third : transactions) {

                if (!third.getFromAccount().equals(second.getToAccount())) {
                        continue;
                }

                // The third transfer must return to the first account.
                if (!third.getToAccount().equals(first.getFromAccount())) {
                        continue;
                }

                // Ensure that these are three different transactions.
                if (first.getTransactionId().equals(second.getTransactionId())
                        || first.getTransactionId().equals(third.getTransactionId())
                        || second.getTransactionId().equals(third.getTransactionId())) {
                        continue;
                }

                // Require transfers to occur in chronological order.
                if (first.getTimestamp().isAfter(second.getTimestamp())
                        || second.getTimestamp().isAfter(third.getTimestamp())) {
                        continue;
                }

                FraudAlert alert = new FraudAlert(
                        first.getFromAccount(),
                        "CIRCULAR_TRANSFER",
                        "A circular transfer pattern was detected across three accounts: "
                                + first.getFromAccount() + " -> "
                                + second.getFromAccount() + " -> "
                                + third.getFromAccount() + " -> "
                                + first.getFromAccount(),
                        0,
                        third.getAmount(),
                        third.getTimestamp(),
                        "HIGH"
                );

                alerts.add(alert);
                }
        }
        }

        return alerts;
    }
}