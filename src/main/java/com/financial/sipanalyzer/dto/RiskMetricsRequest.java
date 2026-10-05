package com.financial.sipanalyzer.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

/**
 * Periodic returns in PERCENT (1.5 = 1.5%), equal length and aligned by period.
 * periodsPerYear: 12 = monthly (default), 52 = weekly, 252 = daily.
 * riskFreeRatePercent defaults to application.yml (6.5).
 */
public record RiskMetricsRequest(
        @NotNull @Size(min = 3) List<@NotNull Double> fundReturns,
        @NotNull @Size(min = 3) List<@NotNull Double> benchmarkReturns,
        @Min(1) @Max(365) Integer periodsPerYear,
        BigDecimal riskFreeRatePercent) {
}
