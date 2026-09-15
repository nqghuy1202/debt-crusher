package org.debtcrusher.ddd.controller.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.debtcrusher.ddd.domain.model.enums.PayoffStrategyType;

import java.math.BigDecimal;

public record PayoffProjectionRequest(
        @NotNull PayoffStrategyType strategy,
        @NotNull @PositiveOrZero BigDecimal extraMonthlyPayment
) {
}
