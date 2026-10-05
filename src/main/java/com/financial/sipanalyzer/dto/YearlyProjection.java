package com.financial.sipanalyzer.dto;

import java.math.BigDecimal;

public record YearlyProjection(int year, BigDecimal monthlySip, BigDecimal totalInvested,
                               BigDecimal wealthGain, BigDecimal finalCorpus) {
}
