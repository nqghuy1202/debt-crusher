package org.debtcrusher.ddd.infrastructure.persistence.jpa;

import org.debtcrusher.ddd.infrastructure.persistence.entity.DebtJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DebtJpaRepository extends JpaRepository<DebtJpaEntity, Long> {
    List<DebtJpaEntity> findByUserId(Long userId);
}
