package com.financial.sipanalyzer.controller;

import com.financial.sipanalyzer.dto.RollingReturnsRequest;
import com.financial.sipanalyzer.dto.RollingReturnsResponse;
import com.financial.sipanalyzer.service.RollingReturnsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/returns")
@Tag(name = "Rolling Returns", description = "Rolling vs trailing returns, consistency and drawdown")
public class RollingReturnsController {
    private final RollingReturnsService service;

    public RollingReturnsController(RollingReturnsService service) {
        this.service = service;
    }

    @PostMapping("/rolling")
    @Operation(summary = "N-year rolling returns, trailing returns, benchmark outperformance % and max drawdown")
    public RollingReturnsResponse analyze(@Valid @RequestBody RollingReturnsRequest request) {
        return service.analyze(request);
    }
}