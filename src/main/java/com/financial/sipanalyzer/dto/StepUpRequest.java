package com.financial.sipanalyzer.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record StepUpRequest(
        @NotNull @DecimalMin("1.0") BigDecimal monthlyInvestment,
        @NotNull @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal annualReturnPercent,
        @Min(1) @Max(60) int years,
        @NotNull @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal stepUpPercent) {
}
