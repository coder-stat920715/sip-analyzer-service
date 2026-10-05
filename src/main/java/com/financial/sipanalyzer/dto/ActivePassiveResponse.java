package com.financial.sipanalyzer.dto;

import com.financial.sipanalyzer.model.Strategy;
import java.math.BigDecimal;
import java.util.List;

public record ActivePassiveResponse(BigDecimal activeTerPercent, BigDecimal passiveTerPercent,
                                    BigDecimal activeNetReturnPercent, BigDecimal passiveNetReturnPercent,
                                    BigDecimal netAlphaPercent, BigDecimal breakEvenGrossOutperformancePercent,
                                    List<HorizonResult> horizons) {

    /**
     * @param corpusDifference   active net corpus minus passive net corpus
     * @param activeExpenseDrag  corpus lost to the active TER (gross corpus - net corpus)
     * @param passiveExpenseDrag corpus lost to the passive TER
     */
    public record HorizonResult(int years, BigDecimal totalInvested, BigDecimal activeNetCorpus,
                                BigDecimal passiveNetCorpus, BigDecimal corpusDifference,
                                BigDecimal activeExpenseDrag, BigDecimal passiveExpenseDrag, Strategy winner) {
    }
}
