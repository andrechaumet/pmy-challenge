package me.andre.orderintake.repository;

import static java.math.BigDecimal.ZERO;
import static me.andre.orderintake.models.enums.OrderType.BUY;
import static me.andre.orderintake.models.enums.OrderType.SELL;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.andre.orderintake.exception.FailedToStoreOrderException;
import me.andre.orderintake.models.domains.Order;
import me.andre.orderintake.models.enums.OrderSymbol;
import me.andre.orderintake.models.enums.OrderType;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class OrderMemoryRepository implements MatchedOrderRepository, PendingOrderRepository {

  private final HashMap<OrderSymbol, HashMap<OrderType, TreeMap<BigDecimal, LinkedHashSet<UUID>>>> pendingMemory;
  private final HashMap<OrderSymbol, HashSet<UUID>> matchedMemory;

  @Override
  public boolean exists(OrderSymbol symbol, UUID id) {
    return Optional.of(matchedMemory.get(symbol))
        .map(ids -> ids.contains(id))
        .orElse(false);
  }

  @Override
  public void increment(OrderSymbol symbol, UUID id, UUID id2) {
    Optional.of(matchedMemory.get(symbol))
        .ifPresent(ordersBySymbol -> {
          ordersBySymbol.add(id);
          ordersBySymbol.add(id2);
        });
  }

  @Override
  public Integer getCount(OrderSymbol symbol) {
    return Optional.of(matchedMemory.get(symbol))
        .map(Set::size)
        .orElse(0);
  }

  @Override
  public Optional<UUID> matchAvailable(Order order) {
    return Optional.of(pendingMemory.get(order.symbol()))
        .map(orderTypes -> orderTypes.get(order.opposite()))
        .map(orderTypePrices -> orderTypePrices.get(order.price()))
        .map(LinkedHashSet::removeFirst);
  }

  // TODO: unify both methods
  @Override
  public void save(Order order) {
    Optional.of(pendingMemory.get(order.symbol()))
        .map(orderTypes -> orderTypes.get(order.type()))
        .map(orderTypePrices -> orderTypePrices.computeIfAbsent(order.price(),
            price -> new LinkedHashSet<>()))
        .map(byId -> byId.add(order.id()))
        .orElseThrow(() -> FailedToStoreOrderException.instance);
  }

  @Override
  public Pair<BigDecimal, BigDecimal> findBounds(OrderSymbol symbol) {
    return Optional.ofNullable(pendingMemory.get(symbol))
        .map(this::calculatePriceRange)
        .orElseThrow();
  }

  private Pair<BigDecimal, BigDecimal> calculatePriceRange(
      HashMap<OrderType, TreeMap<BigDecimal, LinkedHashSet<UUID>>> orderTypes) {
    var buyOrders = orderTypes.get(BUY);
    var sellOrders = orderTypes.get(SELL);
    BigDecimal lowest = minFirstKeys(buyOrders, sellOrders);
    BigDecimal highest = maxLastKeys(buyOrders, sellOrders);
    return Pair.of(lowest, highest);
  }

  private BigDecimal minFirstKeys(TreeMap<BigDecimal, ?> buy, TreeMap<BigDecimal, ?> sell) {
    return Stream.of(firstKeyOrZero(buy), firstKeyOrZero(sell))
        .min(Comparator.naturalOrder())
        .orElse(ZERO);
  }

  private BigDecimal maxLastKeys(TreeMap<BigDecimal, ?> buy, TreeMap<BigDecimal, ?> sell) {
    return Stream.of(lastKeyOrZero(buy), lastKeyOrZero(sell))
        .max(Comparator.naturalOrder())
        .orElse(ZERO);
  }

  private BigDecimal firstKeyOrZero(TreeMap<BigDecimal, ?> orders) {
    return orders.isEmpty() ? ZERO : orders.firstKey();
  }

  private BigDecimal lastKeyOrZero(TreeMap<BigDecimal, ?> orders) {
    return orders.isEmpty() ? ZERO : orders.lastKey();
  }
}