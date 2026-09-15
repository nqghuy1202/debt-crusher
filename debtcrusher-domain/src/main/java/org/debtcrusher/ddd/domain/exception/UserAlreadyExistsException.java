package org.debtcrusher.ddd.domain.exception;

/** Ném khi register bằng email đã tồn tại. */
public class UserAlreadyExistsException extends DomainException {
    public UserAlreadyExistsException(String email) {
        super("Email đã được đăng ký: " + email);
    }
}
