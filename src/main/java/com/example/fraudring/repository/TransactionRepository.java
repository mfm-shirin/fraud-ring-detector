
package com.example.fraudring.repository;

import com.example.fraudring.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository
        extends JpaRepository<Transaction, String> {

    boolean existsByTransactionIdIgnoreCase(String transactionId);
}
