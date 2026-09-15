package org.debtcrusher.ddd.domain.service.impl;

import org.debtcrusher.ddd.domain.model.Debt;
import org.debtcrusher.ddd.domain.model.enums.PayoffStrategyType;
import org.debtcrusher.ddd.domain.service.PayoffStrategy;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/** Ưu tiên khoản có dư nợ nhỏ nhất trước — trả hết nhanh 1 khoản, tạo động lực tâm lý. */
@Service
public class SnowballPayoffStrategy implements PayoffStrategy {

    @Override
    public PayoffStrategyType type() {
        return PayoffStrategyType.SNOWBALL;
    }

    @Override
    public List<Debt> order(List<Debt> debts) {
        return debts.stream()
                .sorted(Comparator.comparing(Debt::getBalance))
                .toList();
    }
}
