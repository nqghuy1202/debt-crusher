package org.debtcrusher.ddd.controller.http;

import org.junit.jupiter.api.Test;
import org.debtcrusher.ddd.application.service.AuthAppService;
import org.debtcrusher.ddd.controller.model.dto.LoginRequest;
import org.debtcrusher.ddd.controller.model.dto.RegisterRequest;
import org.debtcrusher.ddd.controller.security.JwtAuthenticationFilter;
import org.debtcrusher.ddd.controller.security.JwtTokenProvider;
import org.debtcrusher.ddd.controller.security.SecurityConfig;
import org.debtcrusher.ddd.domain.exception.InvalidCredentialsException;
import org.debtcrusher.ddd.domain.exception.UserAlreadyExistsException;
import org.debtcrusher.ddd.domain.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Import thủ công SecurityConfig + JwtTokenProvider + JwtAuthenticationFilter vì @WebMvcTest
 * mặc định chỉ nạp bean thuộc web layer (@Controller/@ControllerAdvice/Filter/...), không tự
 * quét @Configuration/@Component khác — không import thì Spring Security auto-config sẽ áp rule
 * mặc định (secure-everything, form login) thay vì rule thật của app (SecurityConfig).
 */
@WebMvcTest(AuthController.class)
@Import({AuthController.class, SecurityConfig.class, JwtTokenProvider.class, JwtAuthenticationFilter.class})
@TestPropertySource(properties = {
        "jwt.secret=test-jwt-secret-key-must-be-long-enough-for-hs256",
        "jwt.expiration=3600000"
})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthAppService authAppService;

    @Test
    void register_validRequest_returns201WithAccessToken() throws Exception {
        User user = User.reconstitute(1L, "trader@debtcrusher.test", "hash", null);
        when(authAppService.register(anyString(), anyString())).thenReturn(user);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new RegisterRequest("trader@debtcrusher.test", "s3cret-pass"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("trader@debtcrusher.test"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    void register_emailAlreadyExists_returns409() throws Exception {
        when(authAppService.register(anyString(), anyString()))
                .thenThrow(new UserAlreadyExistsException("trader@debtcrusher.test"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new RegisterRequest("trader@debtcrusher.test", "s3cret-pass"))))
                .andExpect(status().isConflict());
    }

    @Test
    void register_invalidEmail_returns400() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new RegisterRequest("not-an-email", "s3cret-pass"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_passwordTooShort_returns400() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new RegisterRequest("trader@debtcrusher.test", "short"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_validCredentials_returns200WithAccessToken() throws Exception {
        User user = User.reconstitute(1L, "trader@debtcrusher.test", "hash", null);
        when(authAppService.login(anyString(), anyString())).thenReturn(user);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new LoginRequest("trader@debtcrusher.test", "s3cret-pass"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    void login_invalidCredentials_returns401() throws Exception {
        when(authAppService.login(anyString(), anyString())).thenThrow(new InvalidCredentialsException());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new LoginRequest("trader@debtcrusher.test", "wrong-pass"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void anyOtherPath_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/trades"))
                .andExpect(status().isUnauthorized());
    }
}
