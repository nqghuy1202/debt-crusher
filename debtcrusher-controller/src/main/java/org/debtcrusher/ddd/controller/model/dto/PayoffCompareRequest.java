package org.debtcrusher.ddd.controller.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record PayoffCompareRequest(
        @NotNull @PositiveOrZero BigDecimal extraMonthlyPayment
) {
}
