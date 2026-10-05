package com.financial.sipanalyzer.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * Gross returns are BEFORE expense ratio. Net return = gross - TER.
 * TERs default to application.yml values (active 0.70, passive 0.20); horizons default to [10, 15, 20].
 */
public record ActivePassiveRequest(
        @NotNull @DecimalMin("1.0") BigDecimal monthlyInvestment,
        @NotNull @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal activeGrossReturnPercent,
        @NotNull @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal passiveGrossReturnPercent,
        @DecimalMin("0.0") @DecimalMax("10.0") BigDecimal activeTerPercent,
        @DecimalMin("0.0") @DecimalMax("10.0") BigDecimal passiveTerPercent,
        List<@Min(1) @Max(60) Integer> horizonYears) {
}
