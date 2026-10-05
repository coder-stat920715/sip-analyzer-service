package com.financial.sipanalyzer.model;

import java.time.LocalDate;

/** Peak-to-trough drawdown. recoveryDate is null if the previous peak was never regained. */
public record DrawdownResult(double maxDrawdownPercent, LocalDate peakDate, LocalDate troughDate,
                             LocalDate recoveryDate) {
}
