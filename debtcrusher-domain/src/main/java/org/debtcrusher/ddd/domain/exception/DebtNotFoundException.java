package org.debtcrusher.ddd.domain.exception;

/** Ném khi tra cứu Debt theo id nhưng không tìm thấy (hoặc không thuộc user hiện tại). */
public class DebtNotFoundException extends DomainException {
    public DebtNotFoundException(Long debtId) {
        super("Không tìm thấy khoản nợ id=" + debtId);
    }
}
