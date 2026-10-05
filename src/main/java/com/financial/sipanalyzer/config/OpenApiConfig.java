package com.financial.sipanalyzer.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI sipAnalyzerOpenApi() {
        return new OpenAPI().info(new Info()
                .title("SIP Analyzer Service")
                .version("1.0.0")
                .description("Quantitative mutual fund & SIP analysis: projections, step-up, rolling returns, "
                        + "active vs passive, risk metrics and glide-path simulation."));
    }
}
