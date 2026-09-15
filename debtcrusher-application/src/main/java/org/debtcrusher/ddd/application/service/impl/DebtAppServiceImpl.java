package org.debtcrusher.ddd.application.service.impl;

import org.debtcrusher.ddd.application.service.DebtAppService;
import org.debtcrusher.ddd.domain.exception.DebtAccessDeniedException;
import org.debtcrusher.ddd.domain.exception.DebtNotFoundException;
import org.debtcrusher.ddd.domain.model.Debt;
import org.debtcrusher.ddd.domain.model.enums.DebtCategory;
import org.debtcrusher.ddd.domain.repository.DebtRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DebtAppServiceImpl implements DebtAppService {

    private final DebtRepository debtRepository;

    public DebtAppServiceImpl(DebtRepository debtRepository) {
        this.debtRepository = debtRepository;
    }

    @Override
    public Debt createDebt(Long userId, String name, BigDecimal balance, BigDecimal annualInterestRatePercent,
                            BigDecimal minimumMonthlyPayment, DebtCategory category) {
        Debt debt = Debt.create(userId, name, balance, annualInterestRatePercent, minimumMonthlyPayment, category);
        return debtRepository.save(debt);
    }

    @Override
    public Debt getDebt(Long userId, Long debtId) {
        Debt debt = debtRepository.findById(debtId).orElseThrow(() -> new DebtNotFoundException(debtId));
        requireOwnership(userId, debt);
        return debt;
    }

    @Override
    public List<Debt> listDebts(Long userId) {
        return debtRepository.findByUserId(userId);
    }

    @Override
    public Debt updateDebt(Long userId, Long debtId, String name, BigDecimal balance,
                            BigDecimal annualInterestRatePercent, BigDecimal minimumMonthlyPayment,
                            DebtCategory category) {
        Debt debt = getDebt(userId, debtId);
        debt.update(name, balance, annualInterestRatePercent, minimumMonthlyPayment, category);
        return debtRepository.save(debt);
    }

    @Override
    public void deleteDebt(Long userId, Long debtId) {
        Debt debt = getDebt(userId, debtId);
        debtRepository.deleteById(debt.getId());
    }

    private void requireOwnership(Long userId, Debt debt) {
        if (!debt.getUserId().equals(userId)) {
            throw new DebtAccessDeniedException(debt.getId());
        }
    }
}
