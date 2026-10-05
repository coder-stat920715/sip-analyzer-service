package com.financial.sipanalyzer.service;

import com.financial.sipanalyzer.config.AnalyzerProperties;
import com.financial.sipanalyzer.dto.ActivePassiveRequest;
import com.financial.sipanalyzer.dto.ActivePassiveResponse;
import com.financial.sipanalyzer.dto.ActivePassiveResponse.HorizonResult;
import com.financial.sipanalyzer.model.Strategy;
import com.financial.sipanalyzer.util.SipMath;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

/** Net return = gross return - TER (standard approximation for annual expense drag). */
@Service
public class ActivePassiveService {
    private static final List<Integer> DEFAULT_HORIZONS = List.of(10, 15, 20);
    private final AnalyzerProperties props;

    public ActivePassiveService(AnalyzerProperties props) {
        this.props = props;
    }

    public ActivePassiveResponse compare(ActivePassiveRequest req) {
        BigDecimal activeTer = req.activeTerPercent() != null ? req.activeTerPercent()
                : props.defaultActiveTerPercent();
        BigDecimal passiveTer = req.passiveTerPercent() != null ? req.passiveTerPercent()
                : props.defaultPassiveTerPercent();
        BigDecimal activeNet = req.activeGrossReturnPercent().subtract(activeTer);
        BigDecimal passiveNet = req.passiveGrossReturnPercent().subtract(passiveTer);
        BigDecimal sip = req.monthlyInvestment();

        List<Integer> horizons = (req.horizonYears() == null || req.horizonYears().isEmpty())
                ? DEFAULT_HORIZONS : req.horizonYears().stream().distinct().sorted().toList();

        List<HorizonResult> results = horizons.stream().map(y -> {
            int months = y * 12;
            BigDecimal invested = SipMath.scale(sip.multiply(BigDecimal.valueOf(months)));
            BigDecimal activeNetCorpus = SipMath.futureValue(sip, activeNet, months);
            BigDecimal passiveNetCorpus = SipMath.futureValue(sip, passiveNet, months);
            BigDecimal activeDrag = SipMath.futureValue(sip, req.activeGrossReturnPercent(), months)
                    .subtract(activeNetCorpus);
            BigDecimal passiveDrag = SipMath.futureValue(sip, req.passiveGrossReturnPercent(), months)
                    .subtract(passiveNetCorpus);
            int cmp = activeNetCorpus.compareTo(passiveNetCorpus);
            Strategy winner = cmp > 0 ? Strategy.ACTIVE : cmp < 0 ? Strategy.PASSIVE : Strategy.TIE;
            return new HorizonResult(y, invested, activeNetCorpus, passiveNetCorpus,
                    activeNetCorpus.subtract(passiveNetCorpus), activeDrag, passiveDrag, winner);
        }).toList();

        return new ActivePassiveResponse(activeTer, passiveTer, activeNet, passiveNet,
                activeNet.subtract(passiveNet), activeTer.subtract(passiveTer), results);
    }
}