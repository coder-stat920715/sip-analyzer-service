package com.financial.sipanalyzer.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import com.financial.sipanalyzer.dto.YearlyProjection;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class SipMathTest {

    @Test
    void futureValueMatchesClosedForm() {
        // P = 10,000, 12% p.a. => i = 1%, n = 120
        double expected = 10_000 * (Math.pow(1.01, 120) - 1) / 0.01 * 1.01;
        BigDecimal fv = SipMath.futureValue(BigDecimal.valueOf(10_000), BigDecimal.valueOf(12), 120);
        assertThat(fv.doubleValue()).isCloseTo(expected, within(0.5));
    }

    @Test
    void zeroReturnIsSimpleSum() {
        BigDecimal fv = SipMath.futureValue(BigDecimal.valueOf(5_000), BigDecimal.ZERO, 24);
        assertThat(fv).isEqualByComparingTo("120000.00");
    }

    @Test
    void stepUpWithZeroPercentEqualsStandard() {
        List<YearlyProjection> s = SipMath.stepUpSchedule(BigDecimal.valueOf(10_000), BigDecimal.valueOf(12),
                BigDecimal.ZERO, 10);
        BigDecimal standard = SipMath.futureValue(BigDecimal.valueOf(10_000), BigDecimal.valueOf(12), 120);
        assertThat(s.get(9).finalCorpus().doubleValue()).isCloseTo(standard.doubleValue(), within(0.05));
    }

    @Test
    void stepUpBeatsStandardAndInvestsMore() {
        List<YearlyProjection> s = SipMath.stepUpSchedule(BigDecimal.valueOf(10_000), BigDecimal.valueOf(12),
                BigDecimal.TEN, 10);
        YearlyProjection last = s.get(9);
        assertThat(last.finalCorpus())
                .isGreaterThan(SipMath.futureValue(BigDecimal.valueOf(10_000), BigDecimal.valueOf(12), 120));
        assertThat(last.totalInvested()).isGreaterThan(BigDecimal.valueOf(1_200_000));
        assertThat(s.get(1).monthlySip()).isEqualByComparingTo("11000.00");
    }
}
