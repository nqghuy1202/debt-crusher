-- Schema cho HL Balance — khớp UserJpaEntity/DebtJpaEntity (debtcrusher-infrastructure).
-- ddl-auto: none (xem application.yaml) nên bảng phải tạo tay bằng script này, chạy tự động khi
-- MySQL container khởi tạo lần đầu (mount vào /docker-entrypoint-initdb.d, xem docker-compose-dev.yml).

CREATE TABLE IF NOT EXISTS users (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    email           VARCHAR(255) NOT NULL,
    password_hash   VARCHAR(255) NOT NULL,
    created_at      TIMESTAMP(6) NOT NULL,
    CONSTRAINT uq_users_email UNIQUE (email)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS debts (
    id                              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id                         BIGINT NOT NULL,
    name                            VARCHAR(100) NOT NULL,
    balance                         DECIMAL(18, 2) NOT NULL,
    annual_interest_rate_percent    DECIMAL(18, 2) NOT NULL,
    minimum_monthly_payment         DECIMAL(18, 2) NOT NULL,
    category                        VARCHAR(20) NOT NULL DEFAULT 'OTHER',
    created_at                      TIMESTAMP(6) NOT NULL,
    CONSTRAINT fk_debts_user FOREIGN KEY (user_id) REFERENCES users (id),
    INDEX idx_debts_user (user_id)
) ENGINE = InnoDB;
