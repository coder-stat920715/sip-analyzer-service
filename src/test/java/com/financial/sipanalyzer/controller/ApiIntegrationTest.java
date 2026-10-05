package com.financial.sipanalyzer.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ApiIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Test
    void projectionReturnsScheduleAndDefaultMilestones() throws Exception {
        mvc.perform(post("/api/v1/sip/projection").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"monthlyInvestment\":10000,\"annualReturnPercent\":12,\"years\":10}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.yearlySchedule.length()").value(10))
                .andExpect(jsonPath("$.milestones.length()").value(4));
    }

    @Test
    void invalidRequestReturns400WithFieldErrors() throws Exception {
        mvc.perform(post("/api/v1/sip/projection").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"monthlyInvestment\":10000,\"annualReturnPercent\":12,\"years\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.years").exists());
    }

    @Test
    void stepUpEndpointWorks() throws Exception {
        mvc.perform(post("/api/v1/sip/step-up").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"monthlyInvestment\":10000,\"annualReturnPercent\":12,\"years\":15,"
                                + "\"stepUpPercent\":10}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.delta.additionalCorpus").isNumber());
    }
}
