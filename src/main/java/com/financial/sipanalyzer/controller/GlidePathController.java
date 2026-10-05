package com.financial.sipanalyzer.controller;

import com.financial.sipanalyzer.dto.GlidePathRequest;
import com.financial.sipanalyzer.dto.GlidePathResponse;
import com.financial.sipanalyzer.service.GlidePathService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/goals")
@Tag(name = "Glide Path", description = "Goal-based equity-to-debt STP glide path simulation")
public class GlidePathController {
    private final GlidePathService service;

    public GlidePathController(GlidePathService service) {
        this.service = service;
    }

    @PostMapping("/glide-path")
    @Operation(summary = "Simulate an STP glide path (equity to debt) and its protection against a market crash")
    public GlidePathResponse simulate(@Valid @RequestBody GlidePathRequest request) {
        return service.simulate(request);
    }
}