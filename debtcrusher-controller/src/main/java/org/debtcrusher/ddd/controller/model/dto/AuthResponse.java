package org.debtcrusher.ddd.controller.model.dto;

/** tokenType luôn "Bearer" — client ghép trực tiếp vào header Authorization. */
public record AuthResponse(String accessToken, String tokenType, long expiresInMs, String email) {
}
