package org.debtcrusher.ddd.domain.model.enums;

/** Phân loại khoản nợ — chỉ để nhóm/lọc trên UI, không ảnh hưởng tính toán payoff. */
public enum DebtCategory {
    CREDIT_CARD,
    CONSUMER_LOAN,
    INSTALLMENT,
    STUDENT_LOAN,
    MORTGAGE,
    OTHER
}
