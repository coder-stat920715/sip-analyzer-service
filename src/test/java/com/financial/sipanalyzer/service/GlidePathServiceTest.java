package com.financial.sipanalyzer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.financial.sipanalyzer.config.AnalyzerProperties;
import com.financial.sipanalyzer.dto.GlidePathRequest;
import com.financial.sipanalyzer.dto.GlidePathResponse;
import com.financial.sipanalyzer.model.Phase;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class GlidePathServiceTest {
    private final GlidePathService service = new GlidePathService(new AnalyzerProperties(
            new BigDecimal("6.5"), new BigDecimal("0.70"), new BigDecimal("0.20"), new BigDecimal("6.5")));

    private GlidePathRequest req(int glideYears) {
        return new GlidePathRequest(BigDecimal.valueOf(10_000), BigDecimal.valueOf(12), BigDecimal.valueOf(6.5),
                15, glideYears, true, BigDecimal.valueOf(30), 12);
    }

    @Test
    void glideProtectsAgainstLateCrashButCostsUpside() {
        GlidePathResponse r = service.simulate(req(3));
        assertThat(r.glidePath().corpusWithCrash()).isGreaterThan(r.fullEquity().corpusWithCrash());
        assertThat(r.fullEquity().corpusWithoutCrash()).isGreaterThan(r.glidePath().corpusWithoutCrash());
        assertThat(r.crashProtectionBenefit()).isPositive();
        assertThat(r.opportunityCostWithoutCrash()).isPositive();
    }

    @Test
    void scheduleMovesFromFullEquityToZeroEquity() {
        GlidePathResponse r = service.simulate(req(3));
        assertThat(r.glideSchedule()).hasSize(15);
        assertThat(r.glideSchedule().get(0).equityAllocationPercent()).isEqualByComparingTo("100");
        assertThat(r.glideSchedule().get(0).phase()).isEqualTo(Phase.ACCUMULATION);
        assertThat(r.glideSchedule().get(14).equityAllocationPercent()).isEqualByComparingTo("0");
        assertThat(r.glideSchedule().get(14).phase()).isEqualTo(Phase.GLIDE);
    }

    @Test
    void zeroGlideYearsEqualsFullEquity() {
        GlidePathResponse r = service.simulate(req(0));
        assertThat(r.glidePath().corpusWithoutCrash()).isEqualByComparingTo(r.fullEquity().corpusWithoutCrash());
        assertThat(r.glidePath().corpusWithCrash()).isEqualByComparingTo(r.fullEquity().corpusWithCrash());
    }

    @Test
    void rejectsGlideLongerThanHorizon() {
        assertThatThrownBy(() -> service.simulate(req(16))).isInstanceOf(IllegalArgumentException.class);
    }
}
