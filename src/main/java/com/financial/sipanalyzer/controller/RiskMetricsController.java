package com.financial.sipanalyzer.controller;

import com.financial.sipanalyzer.dto.RiskMetricsRequest;
import com.financial.sipanalyzer.dto.RiskMetricsResponse;
import com.financial.sipanalyzer.service.RiskMetricsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/risk")
@Tag(name = "Risk Metrics", description = "Alpha, beta, Sharpe ratio and volatility")
public class RiskMetricsController {
    private final RiskMetricsService service;

    public RiskMetricsController(RiskMetricsService service) {
        this.service = service;
    }

    @PostMapping("/metrics")
    @Operation(summary = "Alpha, Beta, Sharpe and annualised standard deviation from periodic returns")
    public RiskMetricsResponse metrics(@Valid @RequestBody RiskMetricsRequest request) {
        return service.calculate(request);
    }
}