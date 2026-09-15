package org.debtcrusher.ddd.controller.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.debtcrusher.ddd.domain.model.enums.DebtCategory;

import java.math.BigDecimal;

/** Dùng chung cho cả tạo (POST /debts) và sửa (PUT /debts/{id}) — cùng 1 bộ field. */
public record DebtRequest(
        @NotBlank String name,
        @NotNull @Positive BigDecimal balance,
        @NotNull @PositiveOrZero BigDecimal annualInterestRatePercent,
        @NotNull @Positive BigDecimal minimumMonthlyPayment,
        @NotNull DebtCategory category
) {
}
