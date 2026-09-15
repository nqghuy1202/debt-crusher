package org.debtcrusher.ddd.infrastructure.persistence.mapper;

import org.debtcrusher.ddd.domain.model.User;
import org.debtcrusher.ddd.infrastructure.persistence.entity.UserJpaEntity;

/** Chuyển đổi thủ công User (domain) <-> UserJpaEntity (persistence) — viết tay thay vì
 * dùng MapStruct vì chỉ 4 field, không đáng thêm dependency/build-step cho project 1-2 ngày. */
public final class UserMapper {

    private UserMapper() {
    }

    public static UserJpaEntity toEntity(User domain)    {
        return UserJpaEntity.builder()
                .id(domain.getId())
                .email(domain.getEmail())
                .passwordHash(domain.getPasswordHash())
                .createdAt(domain.getCreatedAt())
                .build();
    }

    public static User toDomain(UserJpaEntity entity) {
        return User.reconstitute(entity.getId(), entity.getEmail(), entity.getPasswordHash(), entity.getCreatedAt());
    }
}
