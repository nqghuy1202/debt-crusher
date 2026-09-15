package org.debtcrusher.ddd.controller.model.dto;

import org.debtcrusher.ddd.domain.model.enums.PayoffStrategyType;

import java.math.BigDecimal;
import java.util.List;

public record PayoffProjectionResponse(
        PayoffStrategyType strategy,
        int totalMonths,
        BigDecimal totalInterestPaid,
        List<String> payoffOrder
) {
}
