package org.debtcrusher.ddd.domain.service;

import org.debtcrusher.ddd.domain.model.Debt;
import org.debtcrusher.ddd.domain.model.enums.PayoffStrategyType;

import java.util.List;

/**
 * Strategy pattern — mỗi cách ưu tiên trả nợ (Avalanche/Snowball/giữ nguyên thứ tự) là 1
 * implementation. {@link PayoffStrategyFactory} chọn đúng strategy theo {@link PayoffStrategyType}.
 */
public interface PayoffStrategy {

    /** Type mà strategy này xử lý — factory dùng để đăng ký/tra cứu. */
    PayoffStrategyType type();

    /** Sắp lại thứ tự debts theo độ ưu tiên nhận tiền trả thêm (extra payment) mỗi kỳ. */
    List<Debt> order(List<Debt> debts);
}
