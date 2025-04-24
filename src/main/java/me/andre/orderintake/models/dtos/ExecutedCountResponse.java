package me.andre.orderintake.models.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import me.andre.orderintake.models.enums.OrderSymbol;

/**
 * Response object representing the number of executed orders for a given symbol.
 * <p>
 * Typically used in APIs to return execution statistics for trading activity.
 * </p>
 *
 * @param symbol the trading symbol of the order
 * @param count  the number of executed orders
 */
public record ExecutedCountResponse(
    @Schema(description = "Trading symbol of the order", example = "YPFD")
    @NotNull OrderSymbol symbol,
    @Schema(description = "Number of executed orders", example = "12345", minimum = "0")
    @NotNull @PositiveOrZero Integer count
) { }
