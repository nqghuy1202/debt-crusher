package org.debtcrusher.ddd.controller.model.mapper;

import org.debtcrusher.ddd.controller.model.dto.PayoffProjectionResponse;
import org.debtcrusher.ddd.domain.model.PayoffProjection;

public final class PayoffDtoMapper {

    private PayoffDtoMapper() {
    }

    public static PayoffProjectionResponse toResponse(PayoffProjection projection) {
        return new PayoffProjectionResponse(
                projection.getStrategy(),
                projection.getTotalMonths(),
                projection.getTotalInterestPaid(),
                projection.getPayoffOrder()
        );
    }
}
