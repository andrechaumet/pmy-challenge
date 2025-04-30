package me.andre.orderintake.repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import me.andre.orderintake.models.domains.Order;
import me.andre.orderintake.models.enums.OrderSymbol;
import me.andre.orderintake.models.enums.OrderType;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Repository interface for managing pending-to-match orders.
 */
public interface PendingOrderRepository {

  //todo: true

  /**
   * Removes an opposite order that matches the given order and returns true if a match was found.
   *
   * @return id of the matched order in case of a match
   */
  Optional<UUID> removeIfPresent(OrderSymbol symbol, OrderType type, BigDecimal price);

  /**
   * Saves the given order in the repository.
   *
   * @param order the order to be saved
   */
  void save(Order order);

  /**
   * Finds and returns the highest and lowest prices of pending orders for the given symbol.
   *
   * @param symbol the symbol for which the bounds are to be found
   * @return a pair containing the highest price at index 0 and the lowest price at index 1
   */
  Pair<BigDecimal, BigDecimal> findBounds(OrderSymbol symbol);
}