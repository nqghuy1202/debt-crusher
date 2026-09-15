package org.debtcrusher.ddd.infrastructure;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * Module này không có @SpringBootApplication riêng (chỉ debtcrusher-start mới có) — context
 * tối thiểu này chỉ để integration test infrastructure layer chạy được độc lập, trỏ
 * thẳng vào MySQL thật (xem application.yml trong src/test/resources) thay vì mock.
 * Không cần khai báo @EntityScan/@EnableJpaRepositories tường minh: class này nằm ở
 * package cha (org.debtcrusher.ddd.infrastructure) của cả entity lẫn Spring Data repository,
 * nên auto-configuration mặc định của Spring Boot đã quét đúng theo package đó.
 */
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackages = "org.debtcrusher.ddd.infrastructure")
public class InfrastructureTestConfig {
}
