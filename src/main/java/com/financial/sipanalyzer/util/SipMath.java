package com.financial.sipanalyzer.util;

import com.financial.sipanalyzer.dto.YearlyProjection;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/** SIP compounding maths in BigDecimal (DECIMAL128). Contributions are made at the START of each month. */
public final class SipMath {
    public static final MathContext MC = MathContext.DECIMAL128;
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final BigDecimal TWELVE = BigDecimal.valueOf(12);

    private SipMath() { }

    /** i = annual CAGR / 12 / 100 */
    public static BigDecimal monthlyRate(BigDecimal annualPercent) {
        return annualPercent.divide(TWELVE.multiply(HUNDRED), MC);
    }

    /** M = P * ((1+i)^n - 1) / i * (1+i); falls back to P*n when i = 0. */
    public static BigDecimal futureValue(BigDecimal monthlySip, BigDecimal annualPercent, int months) {
        if (months <= 0) {
            return scale(BigDecimal.ZERO);
        }
        BigDecimal i = monthlyRate(annualPercent);
        BigDecimal result;
        if (i.signum() == 0) {
            result = monthlySip.multiply(BigDecimal.valueOf(months));
        } else {
            BigDecimal onePlusI = BigDecimal.ONE.add(i);
            BigDecimal growth = onePlusI.pow(months, MC);
            result = monthlySip.multiply(growth.subtract(BigDecimal.ONE), MC)
                    .divide(i, MC)
                    .multiply(onePlusI, MC);
        }
        return scale(result);
    }

    /**
     * Month-by-month simulation where the monthly SIP rises by stepUpPercent every 12 months.
     * With stepUpPercent = 0 it reproduces {@link #futureValue}.
     */
    public static List<YearlyProjection> stepUpSchedule(BigDecimal initialSip, BigDecimal annualPercent,
                                                        BigDecimal stepUpPercent, int years) {
        BigDecimal growth = BigDecimal.ONE.add(monthlyRate(annualPercent));
        BigDecimal stepFactor = BigDecimal.ONE.add(stepUpPercent.divide(HUNDRED, MC));
        BigDecimal sip = initialSip;
        BigDecimal balance = BigDecimal.ZERO;
        BigDecimal invested = BigDecimal.ZERO;
        List<YearlyProjection> out = new ArrayList<>(years);
        for (int y = 1; y <= years; y++) {
            for (int m = 0; m < 12; m++) {
                balance = balance.add(sip).multiply(growth, MC);
                invested = invested.add(sip);
            }
            BigDecimal investedS = scale(invested);
            BigDecimal corpusS = scale(balance);
            out.add(new YearlyProjection(y, scale(sip), investedS, corpusS.subtract(investedS), corpusS));
            sip = sip.multiply(stepFactor, MC);
        }
        return out;
    }

    public static BigDecimal scale(BigDecimal v) {
        return v.setScale(2, RoundingMode.HALF_UP);
    }
}
