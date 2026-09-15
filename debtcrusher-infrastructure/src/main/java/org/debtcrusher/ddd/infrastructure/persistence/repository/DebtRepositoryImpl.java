package org.debtcrusher.ddd.infrastructure.persistence.repository;

import org.debtcrusher.ddd.domain.model.Debt;
import org.debtcrusher.ddd.domain.repository.DebtRepository;
import org.debtcrusher.ddd.infrastructure.persistence.jpa.DebtJpaRepository;
import org.debtcrusher.ddd.infrastructure.persistence.mapper.DebtMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DebtRepositoryImpl implements DebtRepository {

    private final DebtJpaRepository jpaRepository;

    public DebtRepositoryImpl(DebtJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Debt save(Debt debt) {
        return DebtMapper.toDomain(jpaRepository.save(DebtMapper.toEntity(debt)));
    }

    @Override
    public Optional<Debt> findById(Long id) {
        return jpaRepository.findById(id).map(DebtMapper::toDomain);
    }

    @Override
    public List<Debt> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId).stream().map(DebtMapper::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
