package com.financial.sipanalyzer.service;

import com.financial.sipanalyzer.config.AnalyzerProperties;
import com.financial.sipanalyzer.dto.GlidePathRequest;
import com.financial.sipanalyzer.dto.GlidePathResponse;
import com.financial.sipanalyzer.dto.GlidePathResponse.GlideOutcome;
import com.financial.sipanalyzer.dto.GlidePathResponse.GlideYearPoint;
import com.financial.sipanalyzer.model.Phase;
import com.financial.sipanalyzer.util.SipMath;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Monthly simulation with two buckets (equity, debt). In each glide month, after the SIP is added,
 * equity / remainingMonths is transferred to debt (STP), then both buckets compound. A market crash
 * is applied to the equity bucket at the end of month (totalMonths - crashMonthsBeforeGoal).
 */
@Service
public class GlidePathService {
    private static final int DEFAULT_GLIDE_YEARS = 3;
    private static final BigDecimal DEFAULT_CRASH_PERCENT = BigDecimal.valueOf(30);
    private static final int DEFAULT_CRASH_MONTHS_BEFORE_GOAL = 12;
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private final AnalyzerProperties props;

    public GlidePathService(AnalyzerProperties props) {
        this.props = props;
    }

    private record Params(BigDecimal sip, BigDecimal equityPct, BigDecimal debtPct, int totalMonths,
                          boolean continueSip, BigDecimal crashPct, int crashMonth) { }

    private record Sim(BigDecimal equity, BigDecimal debt, BigDecimal invested, List<GlideYearPoint> points) {
        BigDecimal total() {
            return equity.add(debt);
        }
    }

    public GlidePathResponse simulate(GlidePathRequest req) {
        int glideYears = req.glideYears() != null ? req.glideYears() : DEFAULT_GLIDE_YEARS;
        if (glideYears > req.totalYears()) {
            throw new IllegalArgumentException("glideYears cannot exceed totalYears");
        }
        int totalMonths = req.totalYears() * 12;
        int crashBefore = req.crashMonthsBeforeGoal() != null ? req.crashMonthsBeforeGoal()
                : DEFAULT_CRASH_MONTHS_BEFORE_GOAL;
        if (crashBefore >= totalMonths) {
            throw new IllegalArgumentException("crashMonthsBeforeGoal must be less than the total months");
        }
        BigDecimal crashPct = req.marketCrashPercent() != null ? req.marketCrashPercent() : DEFAULT_CRASH_PERCENT;
        Params p = new Params(req.monthlyInvestment(), req.equityReturnPercent(),
                req.debtReturnPercent() != null ? req.debtReturnPercent() : props.defaultDebtReturnPercent(),
                totalMonths, req.continueSipDuringGlide() == null || req.continueSipDuringGlide(),
                crashPct, totalMonths - crashBefore);

        int glideMonths = glideYears * 12;
        Sim glideClean = run(p, glideMonths, false);
        Sim glideCrash = run(p, glideMonths, true);
        Sim equityClean = run(p, 0, false);
        Sim equityCrash = run(p, 0, true);

        return new GlidePathResponse(req.totalYears(), glideYears, crashPct, crashBefore,
                outcome(glideClean, glideCrash), outcome(equityClean, equityCrash),
                SipMath.scale(glideCrash.total().subtract(equityCrash.total())),
                SipMath.scale(equityClean.total().subtract(glideClean.total())),
                glideClean.points());
    }

    private GlideOutcome outcome(Sim clean, Sim crash) {
        return new GlideOutcome(SipMath.scale(clean.invested()), SipMath.scale(clean.total()),
                SipMath.scale(crash.total()), SipMath.scale(clean.total().subtract(crash.total())));
    }

    private Sim run(Params p, int glideMonths, boolean applyCrash) {
        BigDecimal equityGrowth = BigDecimal.ONE.add(SipMath.monthlyRate(p.equityPct()));
        BigDecimal debtGrowth = BigDecimal.ONE.add(SipMath.monthlyRate(p.debtPct()));
        BigDecimal crashFactor = BigDecimal.ONE.subtract(p.crashPct().divide(HUNDRED, SipMath.MC));
        int glideStart = p.totalMonths() - glideMonths;

        BigDecimal equity = BigDecimal.ZERO;
        BigDecimal debt = BigDecimal.ZERO;
        BigDecimal invested = BigDecimal.ZERO;
        List<GlideYearPoint> points = new ArrayList<>();

        for (int m = 1; m <= p.totalMonths(); m++) {
            boolean inGlide = m > glideStart;
            if (!inGlide || p.continueSip()) {
                equity = equity.add(p.sip());
                invested = invested.add(p.sip());
            }
            if (inGlide) {
                int remaining = p.totalMonths() - m + 1;
                BigDecimal transfer = equity.divide(BigDecimal.valueOf(remaining), SipMath.MC);
                equity = equity.subtract(transfer);
                debt = debt.add(transfer);
            }
            equity = equity.multiply(equityGrowth, SipMath.MC);
            debt = debt.multiply(debtGrowth, SipMath.MC);
            if (applyCrash && m == p.crashMonth()) {
                equity = equity.multiply(crashFactor, SipMath.MC);
            }
            if (m % 12 == 0) {
                BigDecimal total = equity.add(debt);
                BigDecimal allocation = total.signum() == 0 ? BigDecimal.ZERO
                        : equity.multiply(HUNDRED).divide(total, 2, RoundingMode.HALF_UP);
                points.add(new GlideYearPoint(m / 12, inGlide ? Phase.GLIDE : Phase.ACCUMULATION,
                        SipMath.scale(equity), SipMath.scale(debt), SipMath.scale(total), allocation,
                        SipMath.scale(invested)));
            }
        }
        return new Sim(equity, debt, invested, points);
    }
}