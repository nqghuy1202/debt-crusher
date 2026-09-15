package org.debtcrusher.ddd.domain.service.impl;

import org.debtcrusher.ddd.domain.exception.NoActiveDebtsException;
import org.debtcrusher.ddd.domain.exception.PayoffNeverCompletesException;
import org.debtcrusher.ddd.domain.model.Debt;
import org.debtcrusher.ddd.domain.model.PayoffProjection;
import org.debtcrusher.ddd.domain.model.enums.PayoffStrategyType;
import org.debtcrusher.ddd.domain.service.AmortizationCalculator;
import org.debtcrusher.ddd.domain.service.PayoffStrategyFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Mô phỏng tháng-qua-tháng: mỗi tháng tính lãi trên dư nợ còn lại của từng khoản, trả tối thiểu
 * cho mọi khoản đang mở, rồi dồn phần còn lại của "ngân sách" (tổng trả tối thiểu ban đầu +
 * extraMonthlyPayment — giữ cố định suốt vòng đời, đúng bản chất snowball: tiền trả tối thiểu
 * của khoản đã hết nợ tự động dồn sang khoản tiếp theo) vào khoản đầu tiên còn mở theo thứ tự
 * ưu tiên của strategy. Dừng khi hết nợ, hoặc ném lỗi nếu vượt mốc an toàn 50 năm.
 */
@Service
public class AmortizationCalculatorImpl implements AmortizationCalculator {

    private static final int MAX_MONTHS = 600; // 50 năm — vượt mốc này coi như không bao giờ trả hết
    private static final int MONEY_SCALE = 2;

    private final PayoffStrategyFactory strategyFactory;

    public AmortizationCalculatorImpl(PayoffStrategyFactory strategyFactory) {
        this.strategyFactory = strategyFactory;
    }

    @Override
    public PayoffProjection simulate(List<Debt> debts, BigDecimal extraMonthlyPayment, PayoffStrategyType strategyType) {
        if (debts.isEmpty()) {
            throw new NoActiveDebtsException();
        }

        List<Debt> ordered = strategyFactory.getStrategy(strategyType).order(debts);
        List<Working> working = ordered.stream().map(Working::from).toList();

        BigDecimal totalBudget = working.stream()
                .map(w -> w.minPayment)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .add(extraMonthlyPayment);

        BigDecimal totalInterestPaid = BigDecimal.ZERO;
        List<String> payoffOrder = new ArrayList<>();
        int month = 0;

        while (working.stream().anyMatch(Working::isActive)) {
            month++;
            if (month > MAX_MONTHS) {
                throw new PayoffNeverCompletesException();
            }

            for (Working w : working) {
                if (w.isActive()) {
                    BigDecimal interest = w.balance.multiply(w.monthlyRate).setScale(MONEY_SCALE, RoundingMode.HALF_UP);
                    w.balance = w.balance.add(interest);
                    totalInterestPaid = totalInterestPaid.add(interest);
                }
            }

            BigDecimal remainingBudget = totalBudget;
            for (Working w : working) {
                if (w.isActive()) {
                    BigDecimal pay = w.minPayment.min(w.balance);
                    w.balance = w.balance.subtract(pay);
                    remainingBudget = remainingBudget.subtract(pay);
                }
            }
            for (Working w : working) {
                if (remainingBudget.compareTo(BigDecimal.ZERO) <= 0) {
                    break;
                }
                if (w.isActive()) {
                    BigDecimal pay = remainingBudget.min(w.balance);
                    w.balance = w.balance.subtract(pay);
                    remainingBudget = remainingBudget.subtract(pay);
                }
            }
            for (Working w : working) {
                if (w.balance.compareTo(BigDecimal.ZERO) <= 0 && !w.paidOff) {
                    w.balance = BigDecimal.ZERO;
                    w.paidOff = true;
                    payoffOrder.add(w.name);
                }
            }
        }

        return PayoffProjection.builder()
                .strategy(strategyType)
                .totalMonths(month)
                .totalInterestPaid(totalInterestPaid)
                .payoffOrder(payoffOrder)
                .build();
    }

    /** State làm việc nội bộ trong lúc mô phỏng — không lộ ra ngoài calculator. */
    private static final class Working {
        String name;
        BigDecimal balance;
        BigDecimal monthlyRate;
        BigDecimal minPayment;
        boolean paidOff;

        boolean isActive() {
            return !paidOff && balance.compareTo(BigDecimal.ZERO) > 0;
        }

        static Working from(Debt debt) {
            Working w = new Working();
            w.name = debt.getName();
            w.balance = debt.getBalance();
            w.monthlyRate = debt.monthlyInterestRate();
            w.minPayment = debt.getMinimumMonthlyPayment();
            w.paidOff = false;
            return w;
        }
    }
}
