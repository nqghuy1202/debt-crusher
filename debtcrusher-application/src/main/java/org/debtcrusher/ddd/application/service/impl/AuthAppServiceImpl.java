package org.debtcrusher.ddd.application.service.impl;

import org.debtcrusher.ddd.application.service.AuthAppService;
import org.debtcrusher.ddd.domain.exception.InvalidCredentialsException;
import org.debtcrusher.ddd.domain.exception.UserAlreadyExistsException;
import org.debtcrusher.ddd.domain.model.User;
import org.debtcrusher.ddd.domain.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthAppServiceImpl implements AuthAppService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthAppServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User register(String email, String rawPassword) {
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException(email);
        }
        User user = User.register(email, passwordEncoder.encode(rawPassword));
        return userRepository.save(user);
    }

    @Override
    public User login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        return user;
    }
}
