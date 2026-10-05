package com.financial.sipanalyzer.controller;

import com.financial.sipanalyzer.dto.SipProjectionRequest;
import com.financial.sipanalyzer.dto.SipProjectionResponse;
import com.financial.sipanalyzer.dto.StepUpRequest;
import com.financial.sipanalyzer.dto.StepUpResponse;
import com.financial.sipanalyzer.service.SipProjectionService;
import com.financial.sipanalyzer.service.StepUpSipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sip")
@Tag(name = "SIP Projection", description = "Standard and step-up SIP wealth projections")
public class SipController {
    private final SipProjectionService projectionService;
    private final StepUpSipService stepUpService;

    public SipController(SipProjectionService projectionService, StepUpSipService stepUpService) {
        this.projectionService = projectionService;
        this.stepUpService = stepUpService;
    }

    @PostMapping("/projection")
    @Operation(summary = "Project SIP corpus with year-by-year schedule and milestone horizons (5/10/15/20y by default)")
    public SipProjectionResponse project(@Valid @RequestBody SipProjectionRequest request) {
        return projectionService.project(request);
    }

    @PostMapping("/step-up")
    @Operation(summary = "Compare Standard SIP vs Annual Step-Up SIP over the same horizon")
    public StepUpResponse stepUp(@Valid @RequestBody StepUpRequest request) {
        return stepUpService.compare(request);
    }
}