package com.financial.sipanalyzer.service;

import com.financial.sipanalyzer.config.AnalyzerProperties;
import com.financial.sipanalyzer.dto.RiskMetricsRequest;
import com.financial.sipanalyzer.dto.RiskMetricsResponse;
import com.financial.sipanalyzer.util.RiskMath;
import com.financial.sipanalyzer.util.Rounding;
import org.springframework.stereotype.Service;

@Service
public class RiskMetricsService {
    private final AnalyzerProperties props;

    public RiskMetricsService(AnalyzerProperties props) {
        this.props = props;
    }

    public RiskMetricsResponse calculate(RiskMetricsRequest req) {
        if (req.fundReturns().size() != req.benchmarkReturns().size()) {
            throw new IllegalArgumentException("fundReturns and benchmarkReturns must have the same length");
        }
        int ppy = req.periodsPerYear() != null ? req.periodsPerYear() : 12;
        double rf = (req.riskFreeRatePercent() != null ? req.riskFreeRatePercent() : props.riskFreeRatePercent())
                .doubleValue() / 100.0;

        double[] fund = RiskMath.toDecimals(req.fundReturns());
        double[] bench = RiskMath.toDecimals(req.benchmarkReturns());

        double rp = RiskMath.annualizedReturn(fund, ppy);
        double rb = RiskMath.annualizedReturn(bench, ppy);
        double sigma = RiskMath.annualizedVolatility(fund, ppy);
        double sigmaB = RiskMath.annualizedVolatility(bench, ppy);
        double beta = RiskMath.beta(fund, bench);
        double alpha = RiskMath.jensensAlpha(rp, rf, beta, rb);
        double sharpe = RiskMath.sharpe(rp, rf, sigma);

        return new RiskMetricsResponse(fund.length, ppy, Rounding.round2(rf * 100),
                Rounding.round2(rp * 100), Rounding.round2(rb * 100), Rounding.round2((rp - rb) * 100),
                Rounding.round2(alpha * 100), Rounding.round4(beta), Rounding.round2(sigma * 100),
                Rounding.round2(sigmaB * 100), Rounding.round4(sharpe));
    }
}