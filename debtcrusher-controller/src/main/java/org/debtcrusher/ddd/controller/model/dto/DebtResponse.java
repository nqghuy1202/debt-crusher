package org.debtcrusher.ddd.controller.model.dto;

import org.debtcrusher.ddd.domain.model.enums.DebtCategory;

import java.math.BigDecimal;
import java.time.Instant;

public record DebtResponse(
        Long id,
        Long userId,
        String name,
        BigDecimal balance,
        BigDecimal annualInterestRatePercent,
        BigDecimal minimumMonthlyPayment,
        DebtCategory category,
        Instant createdAt
) {
}
