package org.debtcrusher.ddd.domain.repository;

import org.debtcrusher.ddd.domain.model.Debt;

import java.util.List;
import java.util.Optional;

/** Port — implement bởi debtcrusher-infrastructure (Spring Data JPA). */
public interface DebtRepository {

    Debt save(Debt debt);

    Optional<Debt> findById(Long id);

    List<Debt> findByUserId(Long userId);

    void deleteById(Long id);
}
