package me.andre.orderintake.models.domains;

import static me.andre.orderintake.models.enums.OrderType.BUY;
import static me.andre.orderintake.models.enums.OrderType.SELL;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;
import me.andre.orderintake.models.Lockable;
import me.andre.orderintake.models.enums.OrderSymbol;
import me.andre.orderintake.models.enums.OrderType;

/**
 * Represents an order in the Nats trading system.
 * <p>
 * Each order has a {@code price}, a {@code symbol} indicating the traded asset, and a {@code type}
 * specifying whether it's a buy or sell order.
 * </p>
 * <p>
 * This class implements {@link Lockable} to allow locking on a per-symbol and price basis, useful
 * for synchronizing operations on orders targeting the same trading pair and price.
 * </p>
 */
public record Order(
    @NotNull UUID id,
    @NotNull @Positive BigDecimal price,
    @NotNull OrderSymbol symbol,
    @NotNull OrderType type
) implements Lockable {

  /**
   * @return the opposite {@link OrderType}
   */
  public OrderType opposite() {
    return this.type() == BUY ? SELL : BUY;
  }

  /**
   * Lock key for this order. The key is a string composed of the symbol and price, used to identify
   * and lock resources related to this specific order.
   *
   * @return the lock key as a {@link String}
   */
  @Override
  public String lockKey() {
    return String.format("%s.%s", symbol(), price());
  }
}
