package org.debtcrusher.ddd.start;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * scanBasePackages="org.debtcrusher.ddd" chỉ quyết định phạm vi quét bean @Service/@Repository
 * thường — Spring Data JPA tạo bean cho interface *JpaRepository qua cơ chế riêng
 * (@EnableJpaRepositories), mặc định chỉ quét đúng package của class @SpringBootApplication này
 * (org.debtcrusher.ddd.start), KHÔNG tự lan sang nhánh org.debtcrusher.ddd.infrastructure.* dù
 * cùng gốc org.debtcrusher.ddd. Thiếu 2 annotation dưới là UserJpaRepository/DebtJpaRepository
 * không có bean nào cả -> UserRepositoryImpl/DebtRepositoryImpl lỗi autowire lúc khởi động.
 */
@SpringBootApplication(scanBasePackages = "org.debtcrusher.ddd")
@EntityScan(basePackages = "org.debtcrusher.ddd.infrastructure.persistence.entity")
@EnableJpaRepositories(basePackages = "org.debtcrusher.ddd.infrastructure.persistence.jpa")
public class StartApplication {
    public static void main(String[] args) {
        SpringApplication.run(StartApplication.class, args);
    }
}
