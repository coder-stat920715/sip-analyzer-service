package com.financial.sipanalyzer.service;

import com.financial.sipanalyzer.dto.ProjectionSummary;
import com.financial.sipanalyzer.dto.StepUpRequest;
import com.financial.sipanalyzer.dto.StepUpResponse;
import com.financial.sipanalyzer.dto.YearlyProjection;
import com.financial.sipanalyzer.util.SipMath;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.IntStream;
import org.springframework.stereotype.Service;

@Service
public class StepUpSipService {
    private final SipProjectionService projectionService;

    public StepUpSipService(SipProjectionService projectionService) {
        this.projectionService = projectionService;
    }

    public StepUpResponse compare(StepUpRequest req) {
        List<YearlyProjection> standard = IntStream.rangeClosed(1, req.years())
                .mapToObj(y -> projectionService.yearlyProjection(req.monthlyInvestment(),
                        req.annualReturnPercent(), y)).toList();
        List<YearlyProjection> stepUp = SipMath.stepUpSchedule(req.monthlyInvestment(),
                req.annualReturnPercent(), req.stepUpPercent(), req.years());

        ProjectionSummary std = summary(standard.get(standard.size() - 1));
        ProjectionSummary step = summary(stepUp.get(stepUp.size() - 1));

        BigDecimal uplift = std.finalCorpus().signum() == 0 ? BigDecimal.ZERO
                : step.finalCorpus().subtract(std.finalCorpus()).multiply(BigDecimal.valueOf(100))
                .divide(std.finalCorpus(), 2, RoundingMode.HALF_UP);
        StepUpResponse.Delta delta = new StepUpResponse.Delta(
                step.totalInvested().subtract(std.totalInvested()),
                step.wealthGain().subtract(std.wealthGain()),
                step.finalCorpus().subtract(std.finalCorpus()),
                uplift);
        return new StepUpResponse(req.stepUpPercent(), std, step, delta, standard, stepUp);
    }

    private ProjectionSummary summary(YearlyProjection last) {
        return new ProjectionSummary(last.totalInvested(), last.wealthGain(), last.finalCorpus());
    }
}