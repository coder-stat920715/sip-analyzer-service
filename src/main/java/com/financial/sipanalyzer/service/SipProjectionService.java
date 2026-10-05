package com.financial.sipanalyzer.service;

import com.financial.sipanalyzer.dto.SipProjectionRequest;
import com.financial.sipanalyzer.dto.SipProjectionResponse;
import com.financial.sipanalyzer.dto.YearlyProjection;
import com.financial.sipanalyzer.util.SipMath;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.IntStream;
import org.springframework.stereotype.Service;

@Service
public class SipProjectionService {
    private static final List<Integer> DEFAULT_MILESTONES = List.of(5, 10, 15, 20);

    public SipProjectionResponse project(SipProjectionRequest req) {
        BigDecimal sip = req.monthlyInvestment();
        BigDecimal rate = req.annualReturnPercent();
        List<YearlyProjection> schedule = IntStream.rangeClosed(1, req.years())
                .mapToObj(y -> yearlyProjection(sip, rate, y)).toList();
        List<Integer> horizons = (req.milestoneYears() == null || req.milestoneYears().isEmpty())
                ? DEFAULT_MILESTONES : req.milestoneYears();
        List<YearlyProjection> milestones = horizons.stream().distinct().sorted()
                .map(y -> yearlyProjection(sip, rate, y)).toList();
        return new SipProjectionResponse(SipMath.scale(sip), rate, milestones, schedule);
    }

    /** Closed-form projection at the end of the given year. */
    public YearlyProjection yearlyProjection(BigDecimal monthlySip, BigDecimal annualPercent, int year) {
        int months = year * 12;
        BigDecimal invested = SipMath.scale(monthlySip.multiply(BigDecimal.valueOf(months)));
        BigDecimal corpus = SipMath.futureValue(monthlySip, annualPercent, months);
        return new YearlyProjection(year, SipMath.scale(monthlySip), invested, corpus.subtract(invested), corpus);
    }
}
