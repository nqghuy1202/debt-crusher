package org.debtcrusher.ddd.infrastructure.persistence.mapper;

import org.debtcrusher.ddd.domain.model.Debt;
import org.debtcrusher.ddd.infrastructure.persistence.entity.DebtJpaEntity;

/** Chuyển đổi thủ công Debt (domain) <-> DebtJpaEntity, giống UserMapper. */
public final class DebtMapper {

    private DebtMapper() {
    }

    public static DebtJpaEntity toEntity(Debt domain) {
        return DebtJpaEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .name(domain.getName())
                .balance(domain.getBalance())
                .annualInterestRatePercent(domain.getAnnualInterestRatePercent())
                .minimumMonthlyPayment(domain.getMinimumMonthlyPayment())
                .category(domain.getCategory())
                .createdAt(domain.getCreatedAt())
                .build();
    }

    public static Debt toDomain(DebtJpaEntity entity) {
        return Debt.reconstitute(
                entity.getId(),
                entity.getUserId(),
                entity.getName(),
                entity.getBalance(),
                entity.getAnnualInterestRatePercent(),
                entity.getMinimumMonthlyPayment(),
                entity.getCategory(),
                entity.getCreatedAt()
        );
    }
}
