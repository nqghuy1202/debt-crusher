package org.debtcrusher.ddd.application.service;

import org.debtcrusher.ddd.domain.model.PayoffProjection;
import org.debtcrusher.ddd.domain.model.enums.PayoffStrategyType;

import java.math.BigDecimal;
import java.util.List;

/**
 * Tính toán lịch trả nợ (insight chính của HL Balance) trên toàn bộ Debt của 1 user, dựa vào
 * {@link org.debtcrusher.ddd.domain.service.AmortizationCalculator}.
 */
public interface PayoffAppService {

    /** @throws org.debtcrusher.ddd.domain.exception.NoActiveDebtsException nếu user chưa có debt nào */
    PayoffProjection project(Long userId, PayoffStrategyType strategyType, BigDecimal extraMonthlyPayment);

    /**
     * So sánh song song 3 kịch bản: AVALANCHE, SNOWBALL (cả 2 với extraMonthlyPayment) và
     * MINIMUM_ONLY (baseline — chỉ trả tối thiểu, không có extra) — trả lời câu hỏi "chọn
     * strategy nào tiết kiệm được bao nhiêu thời gian/tiền lãi so với việc không làm gì cả".
     */
    List<PayoffProjection> compare(Long userId, BigDecimal extraMonthlyPayment);
}
