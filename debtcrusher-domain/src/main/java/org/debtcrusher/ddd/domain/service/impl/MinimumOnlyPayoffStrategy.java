package org.debtcrusher.ddd.domain.service.impl;

import org.debtcrusher.ddd.domain.model.Debt;
import org.debtcrusher.ddd.domain.model.enums.PayoffStrategyType;
import org.debtcrusher.ddd.domain.service.PayoffStrategy;
import org.springframework.stereotype.Service;

import java.util.List;

/** Baseline dùng để so sánh — không ưu tiên lại, giữ đúng thứ tự debts được truyền vào. */
@Service
public class MinimumOnlyPayoffStrategy implements PayoffStrategy {

    @Override
    public PayoffStrategyType type() {
        return PayoffStrategyType.MINIMUM_ONLY;
    }

    @Override
    public List<Debt> order(List<Debt> debts) {
        return List.copyOf(debts);
    }
}
