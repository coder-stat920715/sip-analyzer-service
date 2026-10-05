package com.financial.sipanalyzer.config;

import java.math.BigDecimal;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/** Externalised defaults (see application.yml, prefix "analyzer"). All values are percentages. */
@ConfigurationProperties(prefix = "analyzer")
public record AnalyzerProperties(
        @DefaultValue("6.5") BigDecimal riskFreeRatePercent,
        @DefaultValue("0.70") BigDecimal defaultActiveTerPercent,
        @DefaultValue("0.20") BigDecimal defaultPassiveTerPercent,
        @DefaultValue("6.5") BigDecimal defaultDebtReturnPercent) {
}
