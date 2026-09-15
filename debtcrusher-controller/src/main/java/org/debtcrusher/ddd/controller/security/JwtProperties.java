package org.debtcrusher.ddd.controller.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Ánh xạ block "jwt.*" trong application.yaml (secret tối thiểu 256-bit, expiration tính bằng ms). */
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(String secret, long expiration) {
}
