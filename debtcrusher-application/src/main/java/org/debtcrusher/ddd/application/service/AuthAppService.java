package org.debtcrusher.ddd.application.service;

import org.debtcrusher.ddd.domain.exception.InvalidCredentialsException;
import org.debtcrusher.ddd.domain.exception.UserAlreadyExistsException;
import org.debtcrusher.ddd.domain.model.User;

/**
 * Orchestrate register/login: domain (User, exception) + infrastructure (UserRepository)
 * qua port, cộng thêm việc hash/verify password (PasswordEncoder). Không biết gì về HTTP/JWT —
 * debtcrusher-controller gọi vào đây lấy {@link User} rồi mới tự phát hành token.
 */
public interface AuthAppService {

    /** @throws UserAlreadyExistsException nếu email đã được đăng ký. */
    User register(String email, String rawPassword);

    /** @throws InvalidCredentialsException nếu email không tồn tại hoặc password sai. */
    User login(String email, String rawPassword);
}
