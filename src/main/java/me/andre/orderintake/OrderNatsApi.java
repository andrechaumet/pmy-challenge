package me.andre.orderintake;

import me.andre.orderintake.models.dtos.CreateOrderRequest;

/**
 * API for receiving market orders via NATS.
 * <p>
 * This interface handles buy and sell orders from different markets. A matching buy/sell pair is
 * found when same symbol and compatible price are paired.
 * <p>
 */
public interface OrderNatsApi {

  /**
   * Processes a new incoming order.
   *
   * @param order to process
   */
  void createOrder(CreateOrderRequest order);
}