package com.financial.sipanalyzer.dto;

import java.math.BigDecimal;

public record ProjectionSummary(BigDecimal totalInvested, BigDecimal wealthGain, BigDecimal finalCorpus) {
}
