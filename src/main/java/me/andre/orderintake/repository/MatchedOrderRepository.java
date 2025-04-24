package me.andre.orderintake.repository;

import java.util.UUID;
import me.andre.orderintake.models.enums.OrderSymbol;

/**
 * Repository interface for managing previously matched orders.
 */
public interface MatchedOrderRepository {

  boolean exists(OrderSymbol symbol, UUID uuid);

  /**
   * Increments the count of matched orders for the given trading symbol.
   *
   * @param symbol the trading symbol of the order to increment the count for
   */
  void increment(OrderSymbol symbol, UUID uuid, UUID uuid2);

  /**
   * Retrieves the count of matched orders for the given trading symbol.
   *
   * @param symbol the trading symbol of the order
   * @return the current count of matched orders for the specified symbol
   */
  Integer getCount(OrderSymbol symbol);
}

