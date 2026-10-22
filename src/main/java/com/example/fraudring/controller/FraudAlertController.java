package com.example.fraudring.controller;

import com.example.fraudring.model.FraudAlert;
import com.example.fraudring.service.FraudDetectionService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/fraud-alerts")
public class FraudAlertController {

    private final FraudDetectionService fraudDetectionService;

    public FraudAlertController(
            FraudDetectionService fraudDetectionService) {
        this.fraudDetectionService = fraudDetectionService;
    }

    @GetMapping
    public List<FraudAlert> getFraudAlerts() {
        return fraudDetectionService.detectFraud();
    }
}

