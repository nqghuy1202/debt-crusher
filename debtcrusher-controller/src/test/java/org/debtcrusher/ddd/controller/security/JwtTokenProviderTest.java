package org.debtcrusher.ddd.controller.security;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtTokenProviderTest {

    // >= 32 byte (256-bit) để HS256 chấp nhận, xem comment jwt.secret trong application.yaml
    private static final String SECRET = "test-jwt-secret-key-must-be-long-enough-for-hs256";

    private final JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(new JwtProperties(SECRET, 3_600_000L));

    @Test
    void generateToken_thenParse_roundTripsUserIdAndEmail() {
        String token = jwtTokenProvider.generateToken(42L, "trader@debtcrusher.test");

        assertThat(jwtTokenProvider.getUserId(token)).isEqualTo(42L);
        assertThat(jwtTokenProvider.getEmail(token)).isEqualTo("trader@debtcrusher.test");
    }

    @Test
    void getUserId_tamperedToken_throwsJwtException() {
        String token = jwtTokenProvider.generateToken(42L, "trader@debtcrusher.test");
        String tampered = token.substring(0, token.length() - 1) + (token.endsWith("A") ? "B" : "A");

        assertThatThrownBy(() -> jwtTokenProvider.getUserId(tampered)).isInstanceOf(JwtException.class);
    }

    @Test
    void getUserId_signedWithDifferentSecret_throwsJwtException() {
        JwtTokenProvider otherProvider = new JwtTokenProvider(new JwtProperties("a-completely-different-secret-key-256-bit-min", 3_600_000L));
        String token = otherProvider.generateToken(42L, "trader@debtcrusher.test");

        assertThatThrownBy(() -> jwtTokenProvider.getUserId(token)).isInstanceOf(JwtException.class);
    }

    @Test
    void getUserId_expiredToken_throwsJwtException() throws InterruptedException {
        JwtTokenProvider shortLivedProvider = new JwtTokenProvider(new JwtProperties(SECRET, 1L));
        String token = shortLivedProvider.generateToken(42L, "trader@debtcrusher.test");
        Thread.sleep(5);

        assertThatThrownBy(() -> shortLivedProvider.getUserId(token)).isInstanceOf(JwtException.class);
    }
}
