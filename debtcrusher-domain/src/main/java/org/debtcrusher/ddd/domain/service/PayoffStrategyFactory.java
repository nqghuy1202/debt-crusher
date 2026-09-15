package org.debtcrusher.ddd.domain.service;

import org.debtcrusher.ddd.domain.model.enums.PayoffStrategyType;

/**
 * Factory Method pattern — application/domain layer chỉ biết interface này, không tự
 * chọn/new trực tiếp implementation nào (đóng-mở: thêm strategy mới không sửa code gọi).
 */
public interface PayoffStrategyFactory {

    PayoffStrategy getStrategy(PayoffStrategyType type);
}
