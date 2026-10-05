package com.financial.sipanalyzer.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

/** A single NAV / index-level observation. */
public record NavPoint(@NotNull LocalDate date, @Positive double nav) {
}
