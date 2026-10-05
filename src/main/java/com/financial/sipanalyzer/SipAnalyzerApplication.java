package com.financial.sipanalyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SipAnalyzerApplication {
    public static void main(String[] args) {
        SpringApplication.run(SipAnalyzerApplication.class, args);
    }
}
