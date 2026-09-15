package org.debtcrusher.ddd.infrastructure.persistence.repository;

import org.debtcrusher.ddd.domain.model.User;
import org.debtcrusher.ddd.domain.repository.UserRepository;
import org.debtcrusher.ddd.infrastructure.persistence.jpa.UserJpaRepository;
import org.debtcrusher.ddd.infrastructure.persistence.mapper.UserMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Adapter implement UserRepository (port ở domain) bằng Spring Data JPA. @Repository
 * (không phải @Service) để Spring bật persistence exception translation (JPA exception
 * -> DataAccessException) cho đúng stereotype của 1 DAO.
 */
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository jpaRepository;

    public UserRepositoryImpl(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public User save(User user) {
        return UserMapper.toDomain(jpaRepository.save(UserMapper.toEntity(user)));
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaRepository.findById(id).map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(UserMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }
}
