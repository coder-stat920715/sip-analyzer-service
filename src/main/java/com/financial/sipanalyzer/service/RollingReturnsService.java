package com.financial.sipanalyzer.service;

import com.financial.sipanalyzer.dto.RollingReturnsRequest;
import com.financial.sipanalyzer.dto.RollingReturnsResponse;
import com.financial.sipanalyzer.dto.RollingReturnsResponse.TrailingReturn;
import com.financial.sipanalyzer.dto.RollingReturnsResponse.WindowAnalysis;
import com.financial.sipanalyzer.util.ReturnsMath;
import com.financial.sipanalyzer.util.Rounding;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.springframework.stereotype.Service;

@Service
public class RollingReturnsService {
    private static final List<Integer> DEFAULT_WINDOWS = List.of(3, 5);
    private static final int[] TRAILING_YEARS = {1, 3, 5, 7, 10};

    public RollingReturnsResponse analyze(RollingReturnsRequest req) {
        TreeMap<LocalDate, Double> fund = ReturnsMath.toSeries(req.fundNavs());
        if (fund.size() < 2) {
            throw new IllegalArgumentException("At least two distinct NAV dates are required");
        }
        TreeMap<LocalDate, Double> bench = (req.benchmarkNavs() == null || req.benchmarkNavs().isEmpty())
                ? null : ReturnsMath.toSeries(req.benchmarkNavs());

        List<TrailingReturn> trailing = new ArrayList<>();
        for (int y : TRAILING_YEARS) {
            Double r = ReturnsMath.trailingReturnPercent(fund, y);
            if (r != null) {
                trailing.add(new TrailingReturn(y, Rounding.round2(r)));
            }
        }
        List<Integer> windows = (req.windowYears() == null || req.windowYears().isEmpty())
                ? DEFAULT_WINDOWS : req.windowYears().stream().distinct().sorted().toList();
        List<WindowAnalysis> analyses = windows.stream().map(w -> analyzeWindow(fund, bench, w)).toList();

        return new RollingReturnsResponse(fund.lastKey(), trailing, analyses,
                ReturnsMath.maxDrawdown(fund), bench == null ? null : ReturnsMath.maxDrawdown(bench));
    }

    private WindowAnalysis analyzeWindow(TreeMap<LocalDate, Double> fund, TreeMap<LocalDate, Double> bench,
                                         int years) {
        TreeMap<LocalDate, Double> rolling = ReturnsMath.rollingReturns(fund, years);
        if (rolling.isEmpty()) {
            return new WindowAnalysis(years, 0, null, null, null, null, null, null, null, 0);
        }
        double[] sorted = rolling.values().stream().mapToDouble(Double::doubleValue).toArray();
        Arrays.sort(sorted);
        int n = sorted.length;
        double avg = Arrays.stream(sorted).average().orElse(0);
        double median = n % 2 == 1 ? sorted[n / 2] : (sorted[n / 2 - 1] + sorted[n / 2]) / 2.0;
        long positive = Arrays.stream(sorted).filter(v -> v > 0).count();

        Double avgBench = null;
        Double pctOutperform = null;
        int compared = 0;
        if (bench != null) {
            TreeMap<LocalDate, Double> benchRolling = ReturnsMath.rollingReturns(bench, years);
            int wins = 0;
            double benchSum = 0;
            for (Map.Entry<LocalDate, Double> e : rolling.entrySet()) {
                Double b = ReturnsMath.valueOnOrBefore(benchRolling, e.getKey(), ReturnsMath.MAX_GAP_DAYS);
                if (b == null) {
                    continue;
                }
                compared++;
                benchSum += b;
                if (e.getValue() > b) {
                    wins++;
                }
            }
            if (compared > 0) {
                avgBench = Rounding.round2(benchSum / compared);
                pctOutperform = Rounding.round2(100.0 * wins / compared);
            }
        }
        return new WindowAnalysis(years, n, Rounding.round2(avg), Rounding.round2(median),
                Rounding.round2(sorted[0]), Rounding.round2(sorted[n - 1]),
                Rounding.round2(100.0 * positive / n), avgBench, pctOutperform, compared);
    }
}
