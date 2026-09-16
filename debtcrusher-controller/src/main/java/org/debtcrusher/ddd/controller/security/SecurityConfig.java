package org.debtcrusher.ddd.controller.security;

import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Stateless JWT auth: không session, không CSRF (API thuần JSON, không có form/cookie).
 * /auth/** (register, login) và các path hạ tầng (actuator, swagger) permitAll, còn lại
 * bắt buộc có JWT hợp lệ.
 *
 * <p><b>Bẫy đã tự dính:</b> JwtAuthenticationFilter là {@code @Component} implement {@link
 * jakarta.servlet.Filter} (qua OncePerRequestFilter) — Spring Boot tự động đăng ký MỌI bean kiểu
 * Filter thành 1 filter cấp servlet-container (qua {@code FilterRegistrationBean} ngầm định),
 * ĐỘC LẬP với việc {@link #filterChain} bên dưới cũng add chính bean này vào trong
 * {@code springSecurityFilterChain}. Kết quả: nó chạy 2 lần — 1 lần đứng ngoài (set Authentication
 * xong bị bỏ), rồi request mới đi vào security chain thật, nơi {@code SecurityContextHolderFilter}
 * nạp lại context rỗng, và lần chạy thứ 2 (đúng vị trí, đáng lẽ set lại) bị OncePerRequestFilter
 * tự chặn vì tưởng "đã chạy request này rồi" — Authentication mất sạch, mọi route bảo vệ luôn 401
 * dù token hợp lệ (đã tự bắt lỗi này bằng debug log, không phải suy đoán). Bean
 * {@link #jwtAuthenticationFilterRegistration} bên dưới tắt đúng cái đăng ký ngầm định đó, để
 * filter CHỈ chạy 1 lần, đúng chỗ, bên trong security chain.</p>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityConfig {

    private static final String[] PUBLIC_PATHS = {
            "/auth/**",
            "/actuator/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final String allowedOrigins;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            @Value("${app.cors.allowed-origins}") String allowedOrigins) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.allowedOrigins = allowedOrigins;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_PATHS).permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(eh -> eh.authenticationEntryPoint(
                        (request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED)))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /** Origin cho phép gọi API kèm header Authorization — cấu hình qua APP_CORS_ALLOWED_ORIGINS
     * (nhiều origin cách nhau bằng dấu phẩy), mặc định chỉ frontend dev (Vite, port 5173). */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.stream(allowedOrigins.split(",")).map(String::trim).toList());
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /** Tắt đăng ký filter cấp servlet-container ngầm định của Boot cho JwtAuthenticationFilter — xem javadoc lớp. */
    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtAuthenticationFilterRegistration(
            JwtAuthenticationFilter filter) {
        FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }
}
