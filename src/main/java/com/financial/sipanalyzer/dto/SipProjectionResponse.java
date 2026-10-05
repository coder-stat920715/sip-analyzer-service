package com.financial.sipanalyzer.dto;

import java.math.BigDecimal;
import java.util.List;

public record SipProjectionResponse(BigDecimal monthlyInvestment, BigDecimal annualReturnPercent,
                                    List<YearlyProjection> milestones, List<YearlyProjection> yearlySchedule) {
}
