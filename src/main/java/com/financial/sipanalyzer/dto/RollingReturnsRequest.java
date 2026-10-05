package com.financial.sipanalyzer.dto;

import com.financial.sipanalyzer.model.NavPoint;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * @param fundNavs      historical fund NAVs (daily/weekly/month-end; any order)
 * @param benchmarkNavs optional benchmark TRI levels (e.g. Nifty Midcap 150 TRI)
 * @param windowYears   rolling windows, default [3, 5]
 */
public record RollingReturnsRequest(
        @NotNull @Size(min = 2) @Valid List<NavPoint> fundNavs,
        @Valid List<NavPoint> benchmarkNavs,
        List<@Min(1) @Max(30) Integer> windowYears) {
}
