package com.financial.sipanalyzer.controller;

import com.financial.sipanalyzer.dto.ActivePassiveRequest;
import com.financial.sipanalyzer.dto.ActivePassiveResponse;
import com.financial.sipanalyzer.service.ActivePassiveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/benchmark")
@Tag(name = "Active vs Passive", description = "Net-of-TER comparison of active funds and index funds")
public class BenchmarkComparisonController {
    private final ActivePassiveService service;

    public BenchmarkComparisonController(ActivePassiveService service) {
        this.service = service;
    }

    @PostMapping("/active-vs-passive")
    @Operation(summary = "Net alpha and corpus comparison after expense ratios over 10-20y horizons")
    public ActivePassiveResponse compare(@Valid @RequestBody ActivePassiveRequest request) {
        return service.compare(request);
    }
}
