package com.financial.sipanalyzer.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.financial.sipanalyzer.config.AnalyzerProperties;
import com.financial.sipanalyzer.dto.ActivePassiveRequest;
import com.financial.sipanalyzer.dto.ActivePassiveResponse;
import com.financial.sipanalyzer.model.Strategy;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ActivePassiveServiceTest {
    private final ActivePassiveService service = new ActivePassiveService(new AnalyzerProperties(
            new BigDecimal("6.5"), new BigDecimal("0.70"), new BigDecimal("0.20"), new BigDecimal("6.5")));

    private ActivePassiveResponse run(String activeGross, String passiveGross) {
        return service.compare(new ActivePassiveRequest(BigDecimal.valueOf(10_000), new BigDecimal(activeGross),
                new BigDecimal(passiveGross), null, null, null));
    }

    @Test
    void activeNeedsHalfPercentGrossOutperformanceToBreakEven() {
        ActivePassiveResponse r = run("14.5", "14.0");
        assertThat(r.breakEvenGrossOutperformancePercent()).isEqualByComparingTo("0.50");
        assertThat(r.netAlphaPercent()).isEqualByComparingTo("0.00");
        assertThat(r.horizons()).hasSize(3);
        assertThat(r.horizons()).allMatch(h -> h.winner() == Strategy.TIE);
    }

    @Test
    void passiveWinsWhenGrossReturnsAreEqual() {
        ActivePassiveResponse r = run("14.0", "14.0");
        assertThat(r.horizons()).allMatch(h -> h.winner() == Strategy.PASSIVE);
        assertThat(r.horizons().get(2).corpusDifference()).isNegative();
    }

    @Test
    void activeWinsWithSufficientAlpha() {
        ActivePassiveResponse r = run("16.0", "14.0");
        assertThat(r.netAlphaPercent()).isEqualByComparingTo("1.50");
        assertThat(r.horizons()).allMatch(h -> h.winner() == Strategy.ACTIVE);
    }
}
