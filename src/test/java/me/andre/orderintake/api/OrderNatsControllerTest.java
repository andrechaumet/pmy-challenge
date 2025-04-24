package me.andre.orderintake.api;

import static java.math.BigDecimal.valueOf;
import static me.andre.orderintake.models.enums.OrderSymbol.BMA;
import static me.andre.orderintake.models.enums.OrderSymbol.CEPU;
import static me.andre.orderintake.models.enums.OrderSymbol.COME;
import static me.andre.orderintake.models.enums.OrderSymbol.GGAL;
import static me.andre.orderintake.models.enums.OrderSymbol.IRSA;
import static me.andre.orderintake.models.enums.OrderSymbol.LOMA;
import static me.andre.orderintake.models.enums.OrderSymbol.MIRG;
import static me.andre.orderintake.models.enums.OrderSymbol.TECO2;
import static me.andre.orderintake.models.enums.OrderSymbol.TRAN;
import static me.andre.orderintake.models.enums.OrderSymbol.YPFD;
import static me.andre.orderintake.models.enums.OrderType.BUY;
import static me.andre.orderintake.models.enums.OrderType.SELL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.TreeMap;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.andre.orderintake.OrderApp;
import me.andre.orderintake.business.OrderMatchService;
import me.andre.orderintake.business.OrderQueryService;
import me.andre.orderintake.models.domains.Order;
import me.andre.orderintake.models.dtos.CreateOrderRequest;
import me.andre.orderintake.models.enums.OrderSymbol;
import me.andre.orderintake.models.enums.OrderType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = OrderApp.class)
@RequiredArgsConstructor
public class OrderNatsControllerTest {

  @Autowired
  private OrderNatsApi orderNatsApi;

  @Autowired
  private OrderQueryService orderQueryService;

  @Autowired
  private OrderMatchService orderMatchService;

  @Autowired
  private HashMap<OrderSymbol, HashMap<OrderType, TreeMap<BigDecimal, LinkedHashSet<UUID>>>> pendingMemory;

  @Autowired
  private HashMap<OrderSymbol, HashSet<UUID>> matchedMemory;

  @Test
  @SneakyThrows
  void testSuccessfulSameSymbolMatch() {
    // GIVEN: All matching orders
    orderNatsApi.createOrder(new CreateOrderRequest(valueOf(100), COME, BUY));
    orderNatsApi.createOrder(new CreateOrderRequest(valueOf(100), COME, BUY));
    orderNatsApi.createOrder(new CreateOrderRequest(valueOf(100), COME, SELL));
    orderNatsApi.createOrder(new CreateOrderRequest(valueOf(100), COME, SELL));
    // WHEN: Requesting count by symbol
    // THEN: All should have matched and QueryService should have the correct executed count
    assertEquals(2, orderQueryService.findExecutedCountBySymbol(COME));
    // AND: No pending orders should be in memory
    assertTrue(pendingMemory.get(YPFD).get(BUY).isEmpty(),
        "There should be no remaining orders");
    assertTrue(pendingMemory.get(YPFD).get(SELL).isEmpty(),
        "There should be no remaining orders");
  }

  @Test
  void testNoMatchDueToDifferentPrices() {
    // GIVEN: A Buy order for "BMA" at 300 a Sell order for "BMA" at 245
    orderNatsApi.createOrder(new CreateOrderRequest(valueOf(300), BMA, BUY));
    orderNatsApi.createOrder(new CreateOrderRequest(valueOf(245), BMA, SELL));
    // WHEN: Both orders are received
    // THEN: No match should occur
    assertEquals(0, orderQueryService.findExecutedCountBySymbol(BMA));
    // AND: Both orders should remain in the order book
    assertFalse(pendingMemory.get(BMA).get(BUY).get(valueOf(300)).isEmpty(),
        "Buy order should remain");
    assertFalse(pendingMemory.get(BMA).get(SELL).get(valueOf(245)).isEmpty(),
        "Sell order should remain");
  }

  @Test
  void testNoMatchDueToDifferentSymbols() {
    // GIVEN: A Buy order for "MIRG" at 300
    orderNatsApi.createOrder(new CreateOrderRequest(valueOf(555), MIRG, BUY));
    // AND: A Sell order for "TECO2" at 300
    orderNatsApi.createOrder(new CreateOrderRequest(valueOf(555), TECO2, SELL));
    // WHEN: Both orders are received
    // THEN: No match should occur
    assertEquals(0, orderQueryService.findExecutedCountBySymbol(MIRG));
    assertEquals(0, orderQueryService.findExecutedCountBySymbol(TECO2));
    // AND: Both orders should remain in the order book
    assertFalse(pendingMemory.get(MIRG).get(BUY).get(valueOf(555)).isEmpty(),
        "Buy order should remain for MIRG");
    assertFalse(pendingMemory.get(TECO2).get(SELL).get(valueOf(555)).isEmpty(),
        "Sell order should remain for TECO2");
  }

  @Test
  void testNoMatchWithOnlyBuyOrders() {
    // GIVEN: Two Buy orders for "IRSA" at 300
    orderNatsApi.createOrder(new CreateOrderRequest(valueOf(300), IRSA, BUY));
    orderNatsApi.createOrder(new CreateOrderRequest(valueOf(300), IRSA, BUY));
    // WHEN: Both orders are received
    // THEN: No match should occur
    assertEquals(0, orderQueryService.findExecutedCountBySymbol(IRSA));
    // AND: Both orders should remain in the order book
    int size = pendingMemory.get(IRSA).get(BUY).get(valueOf(300)).size();
    assertEquals(2, size, "There should be two buy orders remaining");
  }

  @Test
  void testPartialMatchLeavesOneBuyOrder() {
    // GIVEN: Two Buy orders for "LOMA" at 100
    orderNatsApi.createOrder(new CreateOrderRequest(valueOf(100), LOMA, BUY));
    orderNatsApi.createOrder(new CreateOrderRequest(valueOf(100), LOMA, BUY));
    // AND: One Sell order for "LOMA" at 100
    orderNatsApi.createOrder(new CreateOrderRequest(valueOf(100), LOMA, SELL));
    // WHEN: All orders are processed
    // THEN: One Buy order should match the Sell
    assertEquals(1, orderQueryService.findExecutedCountBySymbol(LOMA));
    // AND: One Buy order should remain in the book
    Collection<UUID> remainingBuyOrders = pendingMemory.get(LOMA).get(BUY).get(valueOf(100));
    assertNotNull(remainingBuyOrders, "Remaining buy orders list should not be null");
    assertEquals(1, remainingBuyOrders.size(), "There should be one remaining buy order");
    // AND: No remaining sell orders
    Collection<UUID> remainingSellOrders = pendingMemory.get(LOMA).get(SELL).get(valueOf(100));
    assertTrue(remainingSellOrders == null || remainingSellOrders.isEmpty(),
        "There should be no remaining sell orders");
  }

  @Test
  void testPendingStorageIdempotency() {
    // GIVEN: A buy order for GGAL at 150.00
    Order order = new Order(UUID.randomUUID(), valueOf(50), GGAL, BUY);
    // WHEN: Resending the same two orders to the matcher
    orderMatchService.match(order);
    orderMatchService.match(order);
    Collection<UUID> pendingOrders = pendingMemory.get(GGAL).get(BUY).get(valueOf(50));
    // THEN: No additional matches should occur due to api idempotency
    assertEquals(1, pendingOrders.size());
  }

  @Test
  void testOldestPendingOrderShouldBeFirstToMatch() {
    // GIVEN: Given 4 Buy order for "CEPU"
    Order oldest = new Order(UUID.randomUUID(), valueOf(100), CEPU, BUY);
    // AND: Only one Sell order for "CEPU" at the same price
    CreateOrderRequest orderToMatch = new CreateOrderRequest(valueOf(100), CEPU, SELL);
    // WHEN: Queueing all other orders with same values to match the order
    orderMatchService.match(oldest);
    orderNatsApi.createOrder(new CreateOrderRequest(valueOf(100), CEPU, BUY));
    orderNatsApi.createOrder(new CreateOrderRequest(valueOf(100), CEPU, BUY));
    orderNatsApi.createOrder(new CreateOrderRequest(valueOf(100), CEPU, BUY));
    // AND: A matching order
    orderNatsApi.createOrder(orderToMatch);
    // THEN: The first and oldest pending order should be used to match
    assertTrue(matchedMemory.get(oldest.symbol()).contains(oldest.id()));
    assertEquals(2, matchedMemory.get(oldest.symbol()).size());
  }

  @Test
  void testOrdersMatchedReentryIdempotency() {
    // GIVEN: Two orders to match
    OrderSymbol symbol = TRAN;
    Order order1 = new Order(UUID.randomUUID(), valueOf(100), symbol, BUY);
    Order order2 = new Order(UUID.randomUUID(), valueOf(100), symbol, SELL);
    // WHEN: Attempting to insert them again after having matched
    orderMatchService.match(order1);
    orderMatchService.match(order2);
    // AND: When resending them again
    orderMatchService.match(order1);
    orderMatchService.match(order2);
    // THEN: No additional matches should occur due to api idempotency
    assertEquals(2, matchedMemory.get(symbol).size());
    assertTrue(pendingMemory.get(symbol).get(BUY).get(valueOf(100)).isEmpty());
    assertTrue(pendingMemory.get(symbol).get(SELL).isEmpty());
  }
}
