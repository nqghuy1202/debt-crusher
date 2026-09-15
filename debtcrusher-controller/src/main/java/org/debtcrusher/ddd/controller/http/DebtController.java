package org.debtcrusher.ddd.controller.http;

import jakarta.validation.Valid;
import org.debtcrusher.ddd.application.service.DebtAppService;
import org.debtcrusher.ddd.controller.model.dto.DebtRequest;
import org.debtcrusher.ddd.controller.model.dto.DebtResponse;
import org.debtcrusher.ddd.controller.model.mapper.DebtDtoMapper;
import org.debtcrusher.ddd.domain.model.Debt;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * userId lấy từ JWT (JwtAuthenticationFilter set principal = userId), không nhận từ path/body —
 * tránh 1 user gọi API kèm userId của người khác. DebtAppService tự chặn đọc/sửa/xoá debt không
 * thuộc userId này (DebtAccessDeniedException -> 403, xem GlobalExceptionHandler).
 */
@RestController
@RequestMapping("/debts")
public class DebtController {

    private final DebtAppService debtAppService;

    public DebtController(DebtAppService debtAppService) {
        this.debtAppService = debtAppService;
    }

    @PostMapping
    public ResponseEntity<DebtResponse> create(@AuthenticationPrincipal Long userId,
                                                @Valid @RequestBody DebtRequest request) {
        Debt debt = debtAppService.createDebt(userId, request.name(), request.balance(),
                request.annualInterestRatePercent(), request.minimumMonthlyPayment(), request.category());
        return ResponseEntity.status(HttpStatus.CREATED).body(DebtDtoMapper.toResponse(debt));
    }

    @GetMapping
    public List<DebtResponse> list(@AuthenticationPrincipal Long userId) {
        return debtAppService.listDebts(userId).stream().map(DebtDtoMapper::toResponse).toList();
    }

    @GetMapping("/{debtId}")
    public DebtResponse get(@AuthenticationPrincipal Long userId, @PathVariable Long debtId) {
        return DebtDtoMapper.toResponse(debtAppService.getDebt(userId, debtId));
    }

    @PutMapping("/{debtId}")
    public DebtResponse update(@AuthenticationPrincipal Long userId, @PathVariable Long debtId,
                                @Valid @RequestBody DebtRequest request) {
        Debt debt = debtAppService.updateDebt(userId, debtId, request.name(), request.balance(),
                request.annualInterestRatePercent(), request.minimumMonthlyPayment(), request.category());
        return DebtDtoMapper.toResponse(debt);
    }

    @DeleteMapping("/{debtId}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal Long userId, @PathVariable Long debtId) {
        debtAppService.deleteDebt(userId, debtId);
        return ResponseEntity.noContent().build();
    }
}
