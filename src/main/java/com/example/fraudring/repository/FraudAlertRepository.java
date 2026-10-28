package com.example.fraudring.repository;

import com.example.fraudring.model.FraudAlert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FraudAlertRepository
        extends JpaRepository<FraudAlert, Long> {
}