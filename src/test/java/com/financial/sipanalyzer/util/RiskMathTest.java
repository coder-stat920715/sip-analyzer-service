package com.financial.sipanalyzer.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

import org.junit.jupiter.api.Test;

class RiskMathTest {
    private static final double[] BENCH = {0.01, -0.02, 0.03, 0.015, -0.01, 0.02};

    @Test
    void betaOfIdenticalSeriesIsOne() {
        assertThat(RiskMath.beta(BENCH, BENCH)).isCloseTo(1.0, within(1e-12));
    }

    @Test
    void betaOfLeveragedSeriesIsTwo() {
        double[] fund = new double[BENCH.length];
        for (int i = 0; i < fund.length; i++) {
            fund[i] = BENCH[i] * 2;
        }
        assertThat(RiskMath.beta(fund, BENCH)).isCloseTo(2.0, within(1e-12));
    }

    @Test
    void sampleStdDevAndAnnualisation() {
        double[] x = {0.01, 0.02, 0.03, 0.04, 0.05};
        assertThat(RiskMath.annualizedVolatility(x, 12))
                .isCloseTo(Math.sqrt(0.000250) * Math.sqrt(12), within(1e-12));
    }

    @Test
    void sharpeAndAlpha() {
        assertThat(RiskMath.sharpe(0.15, 0.065, 0.17)).isCloseTo(0.5, within(1e-12));
        assertThat(RiskMath.jensensAlpha(0.15, 0.065, 1.1, 0.12)).isCloseTo(0.15 - (0.065 + 1.1 * 0.055),
                within(1e-12));
        assertThatThrownBy(() -> RiskMath.sharpe(0.1, 0.05, 0)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void annualizedReturnCompoundsGeometrically() {
        double[] monthly = new double[12];
        java.util.Arrays.fill(monthly, 0.01);
        assertThat(RiskMath.annualizedReturn(monthly, 12)).isCloseTo(Math.pow(1.01, 12) - 1, within(1e-12));
    }
}
