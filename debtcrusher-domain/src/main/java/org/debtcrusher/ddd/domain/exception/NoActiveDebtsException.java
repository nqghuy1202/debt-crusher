package org.debtcrusher.ddd.domain.exception;

/** Ném khi user chưa có khoản nợ nào nhưng lại gọi tính payoff projection/compare. */
public class NoActiveDebtsException extends DomainException {
    public NoActiveDebtsException() {
        super("Chưa có khoản nợ nào để tính lịch trả — hãy thêm ít nhất 1 khoản nợ trước");
    }
}
