package org.debtcrusher.ddd.controller;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * debtcrusher-controller không có @SpringBootApplication riêng (chỉ debtcrusher-start mới có) —
 * @WebMvcTest tìm ngược lên package cha của test để lấy 1 @SpringBootConfiguration, thiếu
 * class này thì AuthControllerTest (package org.debtcrusher.ddd.controller.http) không boot được
 * context. Đặt ở package gốc "org.debtcrusher.ddd.controller" để nằm trên đường tìm ngược đó.
 *
 * <p>@ComponentScan chỉ quét package "advice" (GlobalExceptionHandler) — KHÔNG quét "http" (nơi có
 * nhiều controller): thực tế đo được là "controllers = X.class" của @WebMvcTest KHÔNG loại được
 * các @RestController khác lọt vào qua @ComponentScan như tài liệu mô tả (đã kiểm chứng ở dự án
 * gốc: quét cả "http" khiến controller khác bị khởi tạo lẫn trong test, NoSuchBeanDefinitionException
 * vì thiếu app service của controller đó). Vì vậy mỗi @WebMvcTest
 * phải tự @Import đúng 1 controller nó cần, còn GlobalExceptionHandler (không phải @Controller,
 * không bị exclude filter đụng tới) dùng chung qua ComponentScan này cho khỏi lặp lại.</p>
 */
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackages = "org.debtcrusher.ddd.controller.advice")
public class ControllerTestConfig {
}
