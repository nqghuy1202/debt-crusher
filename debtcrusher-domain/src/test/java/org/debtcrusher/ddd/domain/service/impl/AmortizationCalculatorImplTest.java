package org.debtcrusher.ddd.domain.service.impl;

import org.debtcrusher.ddd.domain.exception.NoActiveDebtsException;
import org.debtcrusher.ddd.domain.model.Debt;
import org.debtcrusher.ddd.domain.model.PayoffProjection;
import org.debtcrusher.ddd.domain.model.enums.DebtCategory;
import org.debtcrusher.ddd.domain.model.enums.PayoffStrategyType;
import org.debtcrusher.ddd.domain.service.AmortizationCalculator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AmortizationCalculatorImplTest {

    private final AmortizationCalculator calculator = new AmortizationCalculatorImpl(
            new PayoffStrategyFactoryImpl(List.of(
                    new AvalanchePayoffStrategy(), new SnowballPayoffStrategy(), new MinimumOnlyPayoffStrategy())));

    @Test
    void mot_khoan_no_khong_lai_tra_het_dung_so_thang_du_no_chia_tra_toi_thieu() {
        Debt debt = Debt.create(1L, "Vay 0%", BigDecimal.valueOf(1000), BigDecimal.ZERO, BigDecimal.valueOf(100),
                DebtCategory.OTHER);

        PayoffProjection projection = calculator.simulate(List.of(debt), BigDecimal.ZERO, PayoffStrategyType.MINIMUM_ONLY);

        assertThat(projection.getTotalMonths()).isEqualTo(10);
        assertThat(projection.getTotalInterestPaid()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(projection.getPayoffOrder()).containsExactly("Vay 0%");
    }

    @Test
    void avalanche_uu_tien_lai_suat_cao_con_snowball_uu_tien_du_no_nho() {
        Debt smallBalanceLowRate = Debt.create(1L, "A", BigDecimal.valueOf(500), BigDecimal.valueOf(5),
                BigDecimal.valueOf(50), DebtCategory.OTHER);
        Debt bigBalanceHighRate = Debt.create(1L, "B", BigDecimal.valueOf(2000), BigDecimal.valueOf(20),
                BigDecimal.valueOf(50), DebtCategory.OTHER);
        List<Debt> debts = List.of(smallBalanceLowRate, bigBalanceHighRate);
        BigDecimal extra = BigDecimal.valueOf(200);

        PayoffProjection avalanche = calculator.simulate(debts, extra, PayoffStrategyType.AVALANCHE);
        PayoffProjection snowball = calculator.simulate(debts, extra, PayoffStrategyType.SNOWBALL);

        assertThat(avalanche.getPayoffOrder().get(0)).isEqualTo("B"); // lãi suất cao hơn -> trả trước
        assertThat(snowball.getPayoffOrder().get(0)).isEqualTo("A"); // dư nợ nhỏ hơn -> trả trước
        // Avalanche luôn tối ưu (hoặc bằng) tổng lãi phải trả so với mọi thứ tự khác.
        assertThat(avalanche.getTotalInterestPaid()).isLessThanOrEqualTo(snowball.getTotalInterestPaid());
    }

    @Test
    void khong_co_khoan_no_nao_thi_nem_loi() {
        assertThatThrownBy(() -> calculator.simulate(List.of(), BigDecimal.ZERO, PayoffStrategyType.MINIMUM_ONLY))
                .isInstanceOf(NoActiveDebtsException.class);
    }
}
