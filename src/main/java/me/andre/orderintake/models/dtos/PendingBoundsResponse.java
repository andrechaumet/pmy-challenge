package me.andre.orderintake.models.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import me.andre.orderintake.models.enums.OrderSymbol;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response object representing the pending price bounds for a given trading symbol.
 * <p>
 * This is typically used to return the range of prices for pending orders for a specific trading
 * symbol in an order book.
 * </p>
 *
 * @param symbol  the trading symbol of the order
 * @param lowest  the lowest price bound for pending orders
 * @param highest the highest price bound for pending orders
 */
public record PendingBoundsResponse(
    @Schema(description = "Trading symbol of the order", example = "YPFD")
    @NotNull OrderSymbol symbol,
    @Schema(description = "Lowest price order bound", example = "12.38")
    @NotNull @PositiveOrZero BigDecimal lowest,
    @Schema(description = "Highest price order bound", example = "35.10")
    @NotNull @PositiveOrZero BigDecimal highest
) { }