package org.debtcrusher.ddd.domain.exception;

/** Ném khi field của Debt không hợp lệ (số âm, tên rỗng...). */
public class InvalidDebtRequestException extends DomainException {
    public InvalidDebtRequestException(String message) {
        super(message);
    }
}
