package org.debtcrusher.ddd.domain.service.impl;

import org.debtcrusher.ddd.domain.model.Debt;
import org.debtcrusher.ddd.domain.model.enums.PayoffStrategyType;
import org.debtcrusher.ddd.domain.service.PayoffStrategy;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/** Ưu tiên khoản có lãi suất năm cao nhất trước — tối ưu tổng tiền lãi phải trả. */
@Service
public class AvalanchePayoffStrategy implements PayoffStrategy {

    @Override
    public PayoffStrategyType type() {
        return PayoffStrategyType.AVALANCHE;
    }

    @Override
    public List<Debt> order(List<Debt> debts) {
        return debts.stream()
                .sorted(Comparator.comparing(Debt::getAnnualInterestRatePercent).reversed())
                .toList();
    }
}
