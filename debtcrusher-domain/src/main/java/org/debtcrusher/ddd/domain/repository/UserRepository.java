package org.debtcrusher.ddd.domain.repository;

import org.debtcrusher.ddd.domain.model.User;

import java.util.Optional;

/** Port — implement bởi debtcrusher-infrastructure (JPA). Domain/application chỉ biết interface này. */
public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
