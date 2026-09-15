package org.debtcrusher.ddd.domain.exception;

/**
 * Gốc của mọi exception nghiệp vụ trong domain layer. Application/Controller layer
 * bắt theo loại cụ thể (subclass) để map sang mã lỗi HTTP phù hợp, không phụ thuộc
 * exception kỹ thuật (JPA, HTTP client...) rò rỉ ngược từ infrastructure lên domain.
 */
public abstract class DomainException extends RuntimeException {
    protected DomainException(String message) {
        super(message);
    }
}
