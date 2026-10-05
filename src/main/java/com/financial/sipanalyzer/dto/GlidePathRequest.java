package com.financial.sipanalyzer.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Defaults: debtReturnPercent 6.5, glideYears 3, continueSipDuringGlide true, marketCrashPercent 30,
 * crashMonthsBeforeGoal 12. The STP moves equity to debt in equal monthly instalments
 * (remaining equity / remaining months) so equity is 0% at the goal date.
 */
public record GlidePathRequest(
        @NotNull @DecimalMin("1.0") BigDecimal monthlyInvestment,
        @NotNull @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal equityReturnPercent,
        @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal debtReturnPercent,
        @Min(1) @Max(60) int totalYears,
        @Min(0) @Max(60) Integer glideYears,
        Boolean continueSipDuringGlide,
        @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal marketCrashPercent,
        @Min(0) Integer crashMonthsBeforeGoal) {
}
