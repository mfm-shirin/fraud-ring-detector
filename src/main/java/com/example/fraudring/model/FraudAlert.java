package com.example.fraudring.model;

import java.time.Instant;

public class FraudAlert {

    private String accountId;
    private String alertType;
    private String description;
    private int distinctSenders;
    private double outgoingAmount;
    private Instant detectedAt;
    private String riskLevel;

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
