package org.risklog.ddd.domain.service.impl;

import org.junit.jupiter.api.Test;
import org.risklog.ddd.domain.exception.InvalidPositionSizeRequestException;
import org.risklog.ddd.domain.model.PositionSizeRequest;
import org.risklog.ddd.domain.model.PositionSizeResult;
import org.risklog.ddd.domain.model.enums.Direction;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FixedFractionalPositionSizingStrategyTest {
 
    private final FixedFractionalPositionSizingStrategy strategy = new FixedFractionalPositionSizingStrategy();

    @Test
    void calculate_risk1PercentOn10kAccount_matchesWorkedExample() {
        // accountBalance=10000, risk 1% -> riskAmount=100; |entry-stop|=2 -> size=50 units
        PositionSizeRequest request = PositionSizeRequest.builder()
                .accountBalance(new BigDecimal("10000"))
                .riskPercent(new BigDecimal("1"))
                .entryPrice(new BigDecimal("100"))
                .stopLossPrice(new BigDecimal("98"))
                .direction(Direction.LONG)
                .build();

        PositionSizeResult result = strategy.calculate(request);

        assertThat(result.getRiskAmount()).isEqualByComparingTo("100");
        assertThat(result.getPositionSizeUnits()).isEqualByComparingTo("50");
        assertThat(result.getNotionalValue()).isEqualByComparingTo("5000");
        assertThat(result.getMarginRequired()).isEqualByComparingTo("5000"); // leverage mặc định 1x
    }

    @Test
    void calculate_withLeverage_reducesMarginRequiredOnly() {
        PositionSizeRequest request = PositionSizeRequest.builder()
                .accountBalance(new BigDecimal("10000"))
                .riskPercent(new BigDecimal("1"))
                .entryPrice(new BigDecimal("100"))
                .stopLossPrice(new BigDecimal("98"))
                .direction(Direction.LONG)
                .leverage(new BigDecimal("10"))
                .build();

        PositionSizeResult result = strategy.calculate(request);

        assertThat(result.getNotionalValue()).isEqualByComparingTo("5000");
        assertThat(result.getMarginRequired()).isEqualByComparingTo("500"); // 5000 / 10x
    }

    @Test
    void calculate_entryEqualsStopLoss_throws() {
        PositionSizeRequest request = PositionSizeRequest.builder()
                .accountBalance(new BigDecimal("10000"))
                .riskPercent(new BigDecimal("1"))
                .entryPrice(new BigDecimal("100"))
                .stopLossPrice(new BigDecimal("100"))
                .direction(Direction.LONG)
                .build();

        assertThatThrownBy(() -> strategy.calculate(request))
                .isInstanceOf(InvalidPositionSizeRequestException.class);
    }
}
