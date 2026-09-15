package org.debtcrusher.ddd.domain.exception;

/** Ném khi Debt tồn tại nhưng không thuộc userId đang thao tác. */
public class DebtAccessDeniedException extends DomainException {
    public DebtAccessDeniedException(Long debtId) {
        super("Không có quyền truy cập khoản nợ id=" + debtId);
    }
}
