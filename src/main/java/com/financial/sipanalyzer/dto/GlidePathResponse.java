package com.financial.sipanalyzer.dto;

import com.financial.sipanalyzer.model.Phase;
import java.math.BigDecimal;
import java.util.List;

public record GlidePathResponse(int totalYears, int glideYears, BigDecimal marketCrashPercent,
                                int crashMonthsBeforeGoal, GlideOutcome glidePath, GlideOutcome fullEquity,
                                BigDecimal crashProtectionBenefit, BigDecimal opportunityCostWithoutCrash,
                                List<GlideYearPoint> glideSchedule) {

    /** corpusImpactOfCrash = corpusWithoutCrash - corpusWithCrash. */
    public record GlideOutcome(BigDecimal totalInvested, BigDecimal corpusWithoutCrash,
                               BigDecimal corpusWithCrash, BigDecimal corpusImpactOfCrash) {
    }

    /** Year-end snapshot of the crash-free glide-path run. */
    public record GlideYearPoint(int year, Phase phase, BigDecimal equityBalance, BigDecimal debtBalance,
                                 BigDecimal totalCorpus, BigDecimal equityAllocationPercent,
                                 BigDecimal totalInvested) {
    }
}
