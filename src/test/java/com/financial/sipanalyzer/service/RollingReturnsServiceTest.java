package com.financial.sipanalyzer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import com.financial.sipanalyzer.dto.RollingReturnsRequest;
import com.financial.sipanalyzer.dto.RollingReturnsResponse;
import com.financial.sipanalyzer.model.NavPoint;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class RollingReturnsServiceTest {
    private final RollingReturnsService service = new RollingReturnsService();

    private List<NavPoint> series(double monthlyGrowth) {
        List<NavPoint> pts = new ArrayList<>();
        LocalDate start = LocalDate.of(2018, 1, 1);
        for (int k = 0; k <= 72; k++) {
            pts.add(new NavPoint(start.plusMonths(k), 100 * Math.pow(1 + monthlyGrowth, k)));
        }
        return pts;
    }

    @Test
    void computesRollingStatsAndBenchmarkOutperformance() {
        RollingReturnsResponse res = service.analyze(
                new RollingReturnsRequest(series(0.01), series(0.005), List.of(3, 5)));

        RollingReturnsResponse.WindowAnalysis w3 = res.rollingWindows().get(0);
        assertThat(w3.windowYears()).isEqualTo(3);
        assertThat(w3.observations()).isEqualTo(37);
        assertThat(w3.averageReturnPercent()).isCloseTo((Math.pow(1.01, 12) - 1) * 100, within(0.01));
        assertThat(w3.percentOutperformingBenchmark()).isEqualTo(100.0);
        assertThat(w3.percentPositivePeriods()).isEqualTo(100.0);

        assertThat(res.rollingWindows().get(1).observations()).isEqualTo(13);
        assertThat(res.fundMaxDrawdown().maxDrawdownPercent()).isEqualTo(0.0);
        assertThat(res.trailingReturns()).isNotEmpty();
    }

    @Test
    void shortHistoryYieldsZeroObservations() {
        RollingReturnsResponse res = service.analyze(
                new RollingReturnsRequest(series(0.01), null, List.of(10)));
        assertThat(res.rollingWindows().get(0).observations()).isZero();
        assertThat(res.rollingWindows().get(0).averageReturnPercent()).isNull();
    }
}
