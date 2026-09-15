package org.debtcrusher.ddd.domain.service;

import org.debtcrusher.ddd.domain.model.Debt;
import org.debtcrusher.ddd.domain.model.PayoffProjection;
import org.debtcrusher.ddd.domain.model.enums.PayoffStrategyType;

import java.math.BigDecimal;
import java.util.List;

/**
 * Mô phỏng lịch trả nợ tháng-qua-tháng cho toàn bộ debts của 1 user, dồn extraMonthlyPayment
 * (+ tiền trả tối thiểu của các khoản đã trả hết, tự động "dồn" sang khoản tiếp theo — hiệu ứng
 * snowball) vào khoản được strategyType ưu tiên, tính tổng số tháng và tổng lãi phải trả.
 */
public interface AmortizationCalculator {

    /**
     * @throws org.debtcrusher.ddd.domain.exception.NoActiveDebtsException nếu debts rỗng
     * @throws org.debtcrusher.ddd.domain.exception.PayoffNeverCompletesException nếu vượt mốc
     *         an toàn (50 năm) mà vẫn chưa trả hết — extraMonthlyPayment quá thấp
     */
    PayoffProjection simulate(List<Debt> debts, BigDecimal extraMonthlyPayment, PayoffStrategyType strategyType);
}
