package org.debtcrusher.ddd.domain.model.enums;

/**
 * Thứ tự ưu tiên trả nợ khi có tiền trả thêm (extra payment) mỗi kỳ.
 * AVALANCHE: ưu tiên khoản có lãi suất cao nhất trước (tối ưu tổng tiền lãi phải trả).
 * SNOWBALL: ưu tiên khoản có dư nợ nhỏ nhất trước (tối ưu động lực tâm lý — trả hết nhanh 1 khoản).
 */
public enum PayoffStrategyType {
    AVALANCHE,
    SNOWBALL,
    /** Không ưu tiên lại — giữ đúng thứ tự debts được truyền vào (dùng làm baseline khi so sánh). */
    MINIMUM_ONLY
}
