package com.financial.sipanalyzer.dto;

import com.financial.sipanalyzer.model.DrawdownResult;
import java.time.LocalDate;
import java.util.List;

public record RollingReturnsResponse(LocalDate asOf, List<TrailingReturn> trailingReturns,
                                     List<WindowAnalysis> rollingWindows,
                                     DrawdownResult fundMaxDrawdown, DrawdownResult benchmarkMaxDrawdown) {

    /** Annualised (CAGR) return ending on the latest NAV date. */
    public record TrailingReturn(int years, double annualizedReturnPercent) {
    }

    /** Statistics are null when the history is too short for the window (observations == 0). */
    public record WindowAnalysis(int windowYears, int observations,
                                 Double averageReturnPercent, Double medianReturnPercent,
                                 Double minReturnPercent, Double maxReturnPercent,
                                 Double percentPositivePeriods,
                                 Double averageBenchmarkReturnPercent, Double percentOutperformingBenchmark,
                                 int benchmarkComparedObservations) {
    }
}
