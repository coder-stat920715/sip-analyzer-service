package com.financial.sipanalyzer.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import com.financial.sipanalyzer.model.DrawdownResult;
import java.time.LocalDate;
import java.util.TreeMap;
import org.junit.jupiter.api.Test;

class ReturnsMathTest {

    @Test
    void maxDrawdownFindsPeakTroughAndRecovery() {
        TreeMap<LocalDate, Double> s = new TreeMap<>();
        s.put(LocalDate.of(2020, 1, 1), 100.0);
        s.put(LocalDate.of(2020, 1, 2), 120.0);
        s.put(LocalDate.of(2020, 1, 3), 60.0);
        s.put(LocalDate.of(2020, 1, 4), 130.0);
        DrawdownResult d = ReturnsMath.maxDrawdown(s);
        assertThat(d.maxDrawdownPercent()).isEqualTo(-50.0);
        assertThat(d.peakDate()).isEqualTo(LocalDate.of(2020, 1, 2));
        assertThat(d.troughDate()).isEqualTo(LocalDate.of(2020, 1, 3));
        assertThat(d.recoveryDate()).isEqualTo(LocalDate.of(2020, 1, 4));
    }

    @Test
    void rollingReturnOfDoublingInOneYearIsHundredPercent() {
        TreeMap<LocalDate, Double> s = new TreeMap<>();
        s.put(LocalDate.of(2020, 1, 1), 100.0);
        s.put(LocalDate.of(2021, 1, 1), 200.0);
        TreeMap<LocalDate, Double> r = ReturnsMath.rollingReturns(s, 1);
        assertThat(r).hasSize(1);
        assertThat(r.get(LocalDate.of(2021, 1, 1))).isCloseTo(100.0, within(1e-9));
    }
}
