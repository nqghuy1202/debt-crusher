package org.debtcrusher.ddd.application.service;

import org.debtcrusher.ddd.domain.exception.DebtAccessDeniedException;
import org.debtcrusher.ddd.domain.exception.DebtNotFoundException;
import org.debtcrusher.ddd.domain.model.Debt;
import org.debtcrusher.ddd.domain.model.enums.DebtCategory;

import java.math.BigDecimal;
import java.util.List;

/**
 * Orchestrate CRUD của Debt — luôn kiểm tra Debt thuộc đúng userId đang thao tác trước khi
 * cho đọc/sửa/xoá (không có role/permission, chỉ cô lập dữ liệu giữa các user).
 */
public interface DebtAppService {

    Debt createDebt(Long userId, String name, BigDecimal balance, BigDecimal annualInterestRatePercent,
                     BigDecimal minimumMonthlyPayment, DebtCategory category);

    /**
     * @throws DebtNotFoundException nếu không tồn tại debt nào với id này
     * @throws DebtAccessDeniedException nếu debt tồn tại nhưng không thuộc userId
     */
    Debt getDebt(Long userId, Long debtId);

    List<Debt> listDebts(Long userId);

    /** @throws DebtNotFoundException/DebtAccessDeniedException như {@link #getDebt} */
    Debt updateDebt(Long userId, Long debtId, String name, BigDecimal balance, BigDecimal annualInterestRatePercent,
                     BigDecimal minimumMonthlyPayment, DebtCategory category);

    /** @throws DebtNotFoundException/DebtAccessDeniedException như {@link #getDebt} */
    void deleteDebt(Long userId, Long debtId);
}
