package org.debtcrusher.ddd.domain.exception;

/**
 * Ném khi login sai email hoặc password. Cố tình dùng chung 1 message cho cả 2 trường hợp
 * (email không tồn tại / password sai) — không tiết lộ email nào đã đăng ký hay chưa.
 */
public class InvalidCredentialsException extends DomainException {
    public InvalidCredentialsException() {
        super("Email hoặc password không đúng");
    }
}
