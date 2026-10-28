
package com.example.fraudring.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "fraud_alerts")
public class FraudAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String accountId;

    @Column(nullable = false)
    private String alertType;

    @Column(length = 2000)
    private String description;

    private int distinctSenders;

    private double outgoingAmount;

    @Column(nullable = false)
    private Instant detectedAt;

    @Column(nullable = false)
    private String riskLevel;

    public FraudAlert() {
    }

    public FraudAlert(
            String accountId,
            String alertType,
            String description,
            int distinctSenders,
            double outgoingAmount,
            Instant detectedAt,
            String riskLevel) {

        this.accountId = accountId;
        this.alertType = alertType;
        this.description = description;
        this.distinctSenders = distinctSenders;
        this.outgoingAmount = outgoingAmount;
        this.detectedAt = detectedAt;
        this.riskLevel = riskLevel;
    }

    public Long getId() {
        return id;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getAlertType() {
        return alertType;
    }

    public String getDescription() {
        return description;
    }

    public int getDistinctSenders() {
        return distinctSenders;
    }

    public double getOutgoingAmount() {
        return outgoingAmount;
    }

    public Instant getDetectedAt() {
        return detectedAt;
    }

    public String getRiskLevel() {
        return riskLevel;
    }
}
