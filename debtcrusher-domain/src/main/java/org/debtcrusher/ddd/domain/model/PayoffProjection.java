package org.debtcrusher.ddd.domain.model;

import lombok.Builder;
import lombok.Getter;
import org.debtcrusher.ddd.domain.model.enums.PayoffStrategyType;

import java.math.BigDecimal;
import java.util.List;

/**
 * Kết quả mô phỏng lịch trả nợ theo 1 strategy — value object tính trên-bay (không persist),
 * trả về bởi {@link org.debtcrusher.ddd.domain.service.AmortizationCalculator}. Đây là "insight"
 * chính của HL Balance: so sánh totalMonths/totalInterestPaid giữa các strategy khác nhau.
 */
@Getter
@Builder
public class PayoffProjection {
    /** null khi đây là baseline "chỉ trả tối thiểu" (không áp dụng extra payment theo thứ tự nào). */
    private final PayoffStrategyType strategy;
    private final int totalMonths;
    private final BigDecimal totalInterestPaid;
    /** Tên các khoản nợ theo đúng thứ tự được dồn tiền trả thêm vào, ví dụ [Thẻ tín dụng A, Vay mua xe]. */
    private final List<String> payoffOrder;
}
