package org.debtcrusher.ddd.infrastructure.persistence.jpa;

import org.debtcrusher.ddd.infrastructure.persistence.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/** Spring Data JPA — không lộ ra ngoài infrastructure, UserRepositoryImpl là adapter duy nhất dùng interface này. */
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {

    Optional<UserJpaEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
