package me.andre.orderintake.models.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import me.andre.orderintake.models.enums.OrderSymbol;
import me.andre.orderintake.models.enums.OrderType;

/**
 * Represents an order in the Nats trading system.
 * <p>
 * Each order has a {@code price}, a {@code symbol} indicating the traded asset, and a {@code type}
 * specifying whether it's a buy or sell order.
 * </p>
 */
public record CreateOrderRequest(
    @Schema(description = "Order request price, must be a positive decimal", example = "123.45")
    @NotNull @Positive BigDecimal price,
    @Schema(description = "Symbol of the order, e.g., YPFD", example = "YPFD")
    @NotNull OrderSymbol symbol,
    @Schema(description = "Type of the order, e.g., BUY or SELL", example = "SELL")
    @NotNull OrderType type) { }
