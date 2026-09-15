package org.debtcrusher.ddd.controller.http;

import jakarta.validation.Valid;
import org.debtcrusher.ddd.application.service.PayoffAppService;
import org.debtcrusher.ddd.controller.model.dto.PayoffCompareRequest;
import org.debtcrusher.ddd.controller.model.dto.PayoffProjectionRequest;
import org.debtcrusher.ddd.controller.model.dto.PayoffProjectionResponse;
import org.debtcrusher.ddd.controller.model.mapper.PayoffDtoMapper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Insight chính của HL Balance: mô phỏng lịch trả nợ, so sánh Avalanche/Snowball/chỉ-trả-tối-thiểu. */
@RestController
@RequestMapping("/payoff")
public class PayoffController {

    private final PayoffAppService payoffAppService;

    public PayoffController(PayoffAppService payoffAppService) {
        this.payoffAppService = payoffAppService;
    }

    @PostMapping("/projection")
    public PayoffProjectionResponse projection(@AuthenticationPrincipal Long userId,
                                                @Valid @RequestBody PayoffProjectionRequest request) {
        var projection = payoffAppService.project(userId, request.strategy(), request.extraMonthlyPayment());
        return PayoffDtoMapper.toResponse(projection);
    }

    @PostMapping("/compare")
    public List<PayoffProjectionResponse> compare(@AuthenticationPrincipal Long userId,
                                                    @Valid @RequestBody PayoffCompareRequest request) {
        return payoffAppService.compare(userId, request.extraMonthlyPayment()).stream()
                .map(PayoffDtoMapper::toResponse)
                .toList();
    }
}
