package org.debtcrusher.ddd.domain.exception;

/**
 * Ném khi tổng (trả tối thiểu + trả thêm) không đủ để dư nợ giảm dần — lãi phát sinh mỗi kỳ
 * lớn hơn hoặc bằng số tiền trả, nợ sẽ không bao giờ hết (hoặc vượt mốc an toàn AmortizationCalculator
 * cho phép mô phỏng).
 */
public class PayoffNeverCompletesException extends DomainException {
    public PayoffNeverCompletesException() {
        super("Số tiền trả mỗi kỳ không đủ để giảm dư nợ — hãy tăng số tiền trả thêm mỗi tháng");
    }
}
