package org.debtcrusher.ddd.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.debtcrusher.ddd.domain.model.enums.DebtCategory;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * JPA record cho bảng debts. userId lưu thẳng là cột FK (không @ManyToOne tới UserJpaEntity)
 * vì Debt và User là 2 aggregate khác nhau trong domain — chỉ tham chiếu nhau qua id.
 */
@Entity
@Table(name = "debts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DebtJpaEntity {

    private static final int PRECISION = 18;
    private static final int SCALE = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, precision = PRECISION, scale = SCALE)
    private BigDecimal balance;

    @Column(name = "annual_interest_rate_percent", nullable = false, precision = PRECISION, scale = SCALE)
    private BigDecimal annualInterestRatePercent;

    @Column(name = "minimum_monthly_payment", nullable = false, precision = PRECISION, scale = SCALE)
    private BigDecimal minimumMonthlyPayment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DebtCategory category;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
