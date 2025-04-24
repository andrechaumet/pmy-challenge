package me.andre.orderintake.business;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.andre.orderintake.models.domains.Order;
import me.andre.orderintake.models.enums.OrderSymbol;
import me.andre.orderintake.repository.LockRepository;
import me.andre.orderintake.repository.MatchedOrderRepository;
import me.andre.orderintake.repository.PendingOrderRepository;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class OrderService implements OrderMatchService, OrderQueryService {

  private final PendingOrderRepository pendingOrdersRepository;
  private final MatchedOrderRepository matchedOrdersRepository;
  private final LockRepository<Order> ordersLock;

  public void match(Order order) {
    if (!matchedOrdersRepository.exists(order.symbol(), order.id())) {
      ordersLock.locked(() -> tryMatch(order), order);
    } else {
      log.error("Order with id [{}] was already matched.", order.id());
    }
  }

  public void tryMatch(Order order) {
    pendingOrdersRepository.matchAvailable(order)
        .ifPresentOrElse(orderFound -> matched(order, orderFound), () -> notMatched(order));
  }

  private void matched(Order order1, UUID order2) {
    log.info("Matched order for [{}] with price [{}].", order1.symbol(), order1.price());
    matchedOrdersRepository.increment(order1.symbol(), order1.id(), order2);
  }

  private void notMatched(Order order) {
    log.info("Stored order for [{}] with price [{}].", order.symbol(), order.price());
    pendingOrdersRepository.save(order);
  }

  @Override
  public Integer findExecutedCountBySymbol(OrderSymbol symbol) {
    return matchedOrdersRepository.getCount(symbol) / 2;
  }

  @Override
  public Pair<BigDecimal, BigDecimal> findOrderBounds(OrderSymbol symbol) {
    return pendingOrdersRepository.findBounds(symbol);
  }
}
