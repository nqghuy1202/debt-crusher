package org.debtcrusher.ddd.application.service.impl;

import org.debtcrusher.ddd.application.service.PayoffAppService;
import org.debtcrusher.ddd.domain.model.Debt;
import org.debtcrusher.ddd.domain.model.PayoffProjection;
import org.debtcrusher.ddd.domain.model.enums.PayoffStrategyType;
import org.debtcrusher.ddd.domain.repository.DebtRepository;
import org.debtcrusher.ddd.domain.service.AmortizationCalculator;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PayoffAppServiceImpl implements PayoffAppService {

    private final DebtRepository debtRepository;
    private final AmortizationCalculator amortizationCalculator;

    public PayoffAppServiceImpl(DebtRepository debtRepository, AmortizationCalculator amortizationCalculator) {
        this.debtRepository = debtRepository;
        this.amortizationCalculator = amortizationCalculator;
    }

    @Override
    public PayoffProjection project(Long userId, PayoffStrategyType strategyType, BigDecimal extraMonthlyPayment) {
        List<Debt> debts = debtRepository.findByUserId(userId);
        return amortizationCalculator.simulate(debts, extraMonthlyPayment, strategyType);
    }

    @Override
    public List<PayoffProjection> compare(Long userId, BigDecimal extraMonthlyPayment) {
        List<Debt> debts = debtRepository.findByUserId(userId);
        return List.of(
                amortizationCalculator.simulate(debts, extraMonthlyPayment, PayoffStrategyType.AVALANCHE),
                amortizationCalculator.simulate(debts, extraMonthlyPayment, PayoffStrategyType.SNOWBALL),
                amortizationCalculator.simulate(debts, BigDecimal.ZERO, PayoffStrategyType.MINIMUM_ONLY)
        );
    }
}
