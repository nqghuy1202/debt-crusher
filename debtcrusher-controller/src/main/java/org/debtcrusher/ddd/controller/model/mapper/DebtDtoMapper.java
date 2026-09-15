package org.debtcrusher.ddd.controller.model.mapper;

import org.debtcrusher.ddd.controller.model.dto.DebtResponse;
import org.debtcrusher.ddd.domain.model.Debt;

public final class DebtDtoMapper {

    private DebtDtoMapper() {
    }

    public static DebtResponse toResponse(Debt debt) {
        return new DebtResponse(
                debt.getId(),
                debt.getUserId(),
                debt.getName(),
                debt.getBalance(),
                debt.getAnnualInterestRatePercent(),
                debt.getMinimumMonthlyPayment(),
                debt.getCategory(),
                debt.getCreatedAt()
        );
    }
}
