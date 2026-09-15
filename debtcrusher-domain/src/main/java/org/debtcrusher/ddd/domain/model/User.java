package org.debtcrusher.ddd.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/**
 * Aggregate root cho tài khoản. Đơn giản có chủ đích — auth MVP chỉ để tách dữ liệu
 * giữa các user (xem brief), không có role/permission. accountBalance KHÔNG lưu ở đây,
 * — HL Balance không có khái niệm "account balance" chung, mỗi Debt tự lưu số dư riêng.
 */
@Getter
public class User {

    private final Long id;
    private final String email;
    private final String passwordHash;
    private final Instant createdAt;

    @Builder(access = lombok.AccessLevel.PRIVATE)
    private User(Long id, String email, String passwordHash, Instant createdAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }

    /** Tạo user mới khi register — id để null, infrastructure gán sau khi persist. */
    public static User register(String email, String passwordHash) {
        return User.builder()
                .email(email)
                .passwordHash(passwordHash)
                .createdAt(Instant.now())
                .build();
    }

    /** Dựng lại từ dữ liệu đã persist (dùng bởi mapper ở infrastructure). */
    public static User reconstitute(Long id, String email, String passwordHash, Instant createdAt) {
        return User.builder()
                .id(id)
                .email(email)
                .passwordHash(passwordHash)
                .createdAt(createdAt)
                .build();
    }
}
