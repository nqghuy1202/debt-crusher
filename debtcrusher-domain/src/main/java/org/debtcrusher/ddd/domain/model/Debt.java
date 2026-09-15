package org.debtcrusher.ddd.domain.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import org.debtcrusher.ddd.domain.exception.InvalidDebtRequestException;
import org.debtcrusher.ddd.domain.model.enums.DebtCategory;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Aggregate root cho 1 khoản nợ (thẻ tín dụng, vay tiêu dùng, vay mua xe...). balance/
 * annualInterestRatePercent/minimumMonthlyPayment/category là các field người dùng có thể sửa lại
 * (số dư thực tế thay đổi theo thời gian, lãi suất khuyến mãi hết hạn...), Debt cho phép
 * update qua {@link #update}.
 */
@Getter
public class Debt {

    private final Long id;
    private final Long userId;
    private final Instant createdAt;

    private String name;
    private BigDecimal balance;
    private BigDecimal annualInterestRatePercent;
    private BigDecimal minimumMonthlyPayment;
    private DebtCategory category;

    @Builder(access = AccessLevel.PRIVATE)
    private Debt(Long id, Long userId, String name, BigDecimal balance, BigDecimal annualInterestRatePercent,
                 BigDecimal minimumMonthlyPayment, DebtCategory category, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.balance = balance;
        this.annualInterestRatePercent = annualInterestRatePercent;
        this.minimumMonthlyPayment = minimumMonthlyPayment;
        this.category = category;
        this.createdAt = createdAt;
    }

    public static Debt create(Long userId, String name, BigDecimal balance, BigDecimal annualInterestRatePercent,
                               BigDecimal minimumMonthlyPayment, DebtCategory category) {
        validate(name, balance, annualInterestRatePercent, minimumMonthlyPayment, category);
        return Debt.builder()
                .userId(userId)
                .name(name)
                .balance(balance)
                .annualInterestRatePercent(annualInterestRatePercent)
                .minimumMonthlyPayment(minimumMonthlyPayment)
                .category(category)
                .createdAt(Instant.now())
                .build();
    }

    /** Dựng lại từ dữ liệu đã persist (dùng bởi mapper ở infrastructure). */
    public static Debt reconstitute(Long id, Long userId, String name, BigDecimal balance,
                                     BigDecimal annualInterestRatePercent, BigDecimal minimumMonthlyPayment,
                                     DebtCategory category, Instant createdAt) {
        return Debt.builder()
                .id(id)
                .userId(userId)
                .name(name)
                .balance(balance)
                .annualInterestRatePercent(annualInterestRatePercent)
                .minimumMonthlyPayment(minimumMonthlyPayment)
                .category(category)
                .createdAt(createdAt)
                .build();
    }

    /** Sửa lại thông tin khoản nợ (số dư thực tế, lãi suất, trả tối thiểu đổi theo thời gian). */
    public void update(String name, BigDecimal balance, BigDecimal annualInterestRatePercent,
                        BigDecimal minimumMonthlyPayment, DebtCategory category) {
        validate(name, balance, annualInterestRatePercent, minimumMonthlyPayment, category);
        this.name = name;
        this.balance = balance;
        this.annualInterestRatePercent = annualInterestRatePercent;
        this.minimumMonthlyPayment = minimumMonthlyPayment;
        this.category = category;
    }

    /** Lãi phát sinh trong 1 kỳ (1 tháng) trên dư nợ hiện tại — dùng bởi AmortizationCalculator. */
    public BigDecimal monthlyInterestRate() {
        return annualInterestRatePercent
                .divide(BigDecimal.valueOf(100), 10, java.math.RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 10, java.math.RoundingMode.HALF_UP);
    }

    private static void validate(String name, BigDecimal balance, BigDecimal annualInterestRatePercent,
                                  BigDecimal minimumMonthlyPayment, DebtCategory category) {
        if (name == null || name.isBlank()) {
            throw new InvalidDebtRequestException("name không được để trống");
        }
        if (category == null) {
            throw new InvalidDebtRequestException("category không được để trống");
        }
        requirePositive(balance, "balance");
        requireNonNegative(annualInterestRatePercent, "annualInterestRatePercent");
        requirePositive(minimumMonthlyPayment, "minimumMonthlyPayment");
    }

    private static void requirePositive(BigDecimal value, String field) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidDebtRequestException(field + " phải là số dương");
        }
    }

    private static void requireNonNegative(BigDecimal value, String field) {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidDebtRequestException(field + " không được âm");
        }
    }
}
