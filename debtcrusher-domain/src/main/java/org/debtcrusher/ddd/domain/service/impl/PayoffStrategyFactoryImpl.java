package org.debtcrusher.ddd.domain.service.impl;

import org.debtcrusher.ddd.domain.model.enums.PayoffStrategyType;
import org.debtcrusher.ddd.domain.service.PayoffStrategy;
import org.debtcrusher.ddd.domain.service.PayoffStrategyFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Spring tự inject toàn bộ bean implement PayoffStrategy vào đây (mỗi strategy tự khai báo
 * @Service) — thêm 1 cách ưu tiên trả nợ mới chỉ cần viết thêm 1 class implement interface,
 * không phải sửa factory này (Open/Closed).
 */
@Service
public class PayoffStrategyFactoryImpl implements PayoffStrategyFactory {

    private final Map<PayoffStrategyType, PayoffStrategy> strategiesByType;

    public PayoffStrategyFactoryImpl(List<PayoffStrategy> strategies) {
        this.strategiesByType = strategies.stream()
                .collect(Collectors.toUnmodifiableMap(PayoffStrategy::type, Function.identity()));
    }

    @Override
    public PayoffStrategy getStrategy(PayoffStrategyType type) {
        PayoffStrategy strategy = strategiesByType.get(type);
        if (strategy == null) {
            throw new IllegalStateException("Không có PayoffStrategy nào đăng ký cho type=" + type);
        }
        return strategy;
    }
}
