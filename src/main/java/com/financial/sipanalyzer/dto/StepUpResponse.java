package com.financial.sipanalyzer.dto;

import java.math.BigDecimal;
import java.util.List;

public record StepUpResponse(BigDecimal stepUpPercent, ProjectionSummary standard, ProjectionSummary stepUp,
                             Delta delta, List<YearlyProjection> standardSchedule,
                             List<YearlyProjection> stepUpSchedule) {

    /** Step-Up minus Standard. */
    public record Delta(BigDecimal additionalInvestment, BigDecimal additionalWealthGain,
                        BigDecimal additionalCorpus, BigDecimal corpusUpliftPercent) {
    }
}
