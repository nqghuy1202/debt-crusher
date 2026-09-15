package org.debtcrusher.ddd.controller.http;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.validation.Valid;
import org.debtcrusher.ddd.application.service.AuthAppService;
import org.debtcrusher.ddd.controller.model.dto.AuthResponse;
import org.debtcrusher.ddd.controller.model.dto.LoginRequest;
import org.debtcrusher.ddd.controller.model.dto.RegisterRequest;
import org.debtcrusher.ddd.controller.security.JwtTokenProvider;
import org.debtcrusher.ddd.domain.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * RateLimiter "authApi" (cấu hình ở application.yaml) chống brute-force trên cả 2 endpoint —
 * register cũng đáng giới hạn vì có thể bị dùng để dò email đã tồn tại (409) hàng loạt.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthAppService authAppService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(AuthAppService authAppService, JwtTokenProvider jwtTokenProvider) {
        this.authAppService = authAppService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/register")
    @RateLimiter(name = "authApi")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = authAppService.register(request.email(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(toAuthResponse(user));
    }

    @PostMapping("/login")
    @RateLimiter(name = "authApi")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        User user = authAppService.login(request.email(), request.password());
        return toAuthResponse(user);
    }

    private AuthResponse toAuthResponse(User user) {
        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail());
        return new AuthResponse(token, "Bearer", jwtTokenProvider.getExpirationMs(), user.getEmail());
    }
}
