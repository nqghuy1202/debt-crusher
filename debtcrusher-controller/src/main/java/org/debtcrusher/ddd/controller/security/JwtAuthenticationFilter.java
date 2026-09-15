package org.debtcrusher.ddd.controller.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Đọc "Authorization: Bearer <token>", set principal = userId (Long) vào SecurityContext nếu
 * token hợp lệ. Không có role/permission (xem brief auth MVP) nên authorities luôn rỗng —
 * @anyRequest().authenticated() ở SecurityConfig chỉ cần biết "đã login hay chưa".
 *
 * <p>Token thiếu/sai/hết hạn: KHÔNG chặn request ở đây (không throw) — để SecurityConfig tự
 * quyết định route đó có bắt buộc auth hay không (vd /auth/** vẫn permitAll dù không có token).</p>
 *
 * <p>Bắt buộc dùng {@link SecurityContextHolder#setContext(SecurityContext)} với 1 context mới,
 * KHÔNG được chỉ mutate {@code SecurityContextHolder.getContext().setAuthentication(...)}: từ
 * Spring Security 6, {@code SecurityContextHolderFilter} nạp context theo kiểu deferred/lazy — gọi
 * {@code getContext()} nhiều lần có thể trả về nhiều instance rỗng khác nhau (chưa cache), mutate
 * 1 instance rồi bỏ đó sẽ bị {@code AnonymousAuthenticationFilter} phía sau ghi đè mất, request
 * luôn rớt về 401 dù token hợp lệ (đã tự kiểm chứng lỗi này bằng debug log trước khi sửa).</p>
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith(BEARER_PREFIX)) {
            String token = header.substring(BEARER_PREFIX.length());
            try {
                Long userId = jwtTokenProvider.getUserId(token);
                var authentication = new UsernamePasswordAuthenticationToken(userId, null, List.of());
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);
            } catch (JwtException | IllegalArgumentException e) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
