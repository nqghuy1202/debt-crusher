package org.debtcrusher.ddd.application.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.debtcrusher.ddd.domain.exception.InvalidCredentialsException;
import org.debtcrusher.ddd.domain.exception.UserAlreadyExistsException;
import org.debtcrusher.ddd.domain.model.User;
import org.debtcrusher.ddd.domain.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Dùng BCryptPasswordEncoder thật (không mock) — hash/verify là đúng thứ cần chốt hành vi,
 * mock nó chỉ còn test lại chính logic của test.
 */
@ExtendWith(MockitoExtension.class)
class AuthAppServiceImplTest {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Mock
    private UserRepository userRepository;

    private AuthAppServiceImpl authAppService;

    @BeforeEach
    void setUp() {
        authAppService = new AuthAppServiceImpl(userRepository, passwordEncoder);
    }

    @Test
    void register_newEmail_hashesPasswordAndSaves() {
        when(userRepository.existsByEmail("trader@debtcrusher.test")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User saved = authAppService.register("trader@debtcrusher.test", "s3cret-pass");

        assertThat(saved.getEmail()).isEqualTo("trader@debtcrusher.test");
        assertThat(saved.getPasswordHash()).isNotEqualTo("s3cret-pass");
        assertThat(passwordEncoder.matches("s3cret-pass", saved.getPasswordHash())).isTrue();
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_emailAlreadyExists_throwsUserAlreadyExists() {
        when(userRepository.existsByEmail("trader@debtcrusher.test")).thenReturn(true);

        assertThatThrownBy(() -> authAppService.register("trader@debtcrusher.test", "s3cret-pass"))
                .isInstanceOf(UserAlreadyExistsException.class);
    }

    @Test
    void login_correctPassword_returnsUser() {
        String hash = passwordEncoder.encode("s3cret-pass");
        User existing = User.reconstitute(1L, "trader@debtcrusher.test", hash, null);
        when(userRepository.findByEmail("trader@debtcrusher.test")).thenReturn(Optional.of(existing));

        User result = authAppService.login("trader@debtcrusher.test", "s3cret-pass");

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void login_wrongPassword_throwsInvalidCredentials() {
        String hash = passwordEncoder.encode("s3cret-pass");
        User existing = User.reconstitute(1L, "trader@debtcrusher.test", hash, null);
        when(userRepository.findByEmail("trader@debtcrusher.test")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> authAppService.login("trader@debtcrusher.test", "wrong-pass"))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void login_unknownEmail_throwsInvalidCredentials() {
        when(userRepository.findByEmail("ghost@debtcrusher.test")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authAppService.login("ghost@debtcrusher.test", "whatever"))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}
