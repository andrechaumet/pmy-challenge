package me.andre.orderintake.api;

import static java.math.BigDecimal.valueOf;
import static me.andre.orderintake.models.enums.OrderSymbol.CRES;
import static me.andre.orderintake.models.enums.OrderSymbol.IRSA;
import static me.andre.orderintake.models.enums.OrderSymbol.SUPV;
import static me.andre.orderintake.models.enums.OrderSymbol.YPFD;
import static me.andre.orderintake.models.enums.OrderType.BUY;
import static me.andre.orderintake.models.enums.OrderType.SELL;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.SneakyThrows;
import me.andre.orderintake.models.dtos.CreateOrderRequest;
import me.andre.orderintake.models.enums.OrderSymbol;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderRestControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private OrderNatsApi orderNatsApi;

  @Test
  @SneakyThrows
  void testSuccessfulExecutedOrderEndpoint() {
    OrderSymbol symbol = YPFD;
    orderNatsApi.createOrder(
        new CreateOrderRequest(valueOf(100), symbol, BUY));
    orderNatsApi.createOrder(
        new CreateOrderRequest(valueOf(100), symbol, SELL));
    // GIVEN: A valid symbol in a valid endpoint
    String endpoint = "/orders/{symbol}/executed-count".replace("{symbol}", symbol.toString());
    // WHEN: finding executed orders count
    mockMvc.perform(get(endpoint))
        // THEN: count with respective symbol should be returned
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.count").value(1))
        .andExpect(jsonPath("$.symbol").value(symbol.toString()));
  }

  @Test
  @SneakyThrows
  void testSymbolNotFoundExecutedOrderEndpoint() {
    // GIVEN: a non existing symbol
    String symbol = "ANDY";
    String endpoint = "/orders/{symbol}/executed-count".replace("{symbol}", symbol);
    // WHEN: finding executed orders count
    mockMvc.perform(get(endpoint))
        // THEN: 404 symbol not found should be returned
        .andExpect(status().isNotFound());
  }

  @Test
  @SneakyThrows
  void testSuccessfulPendingOrderBoundEndpoint() {
    // GIVEN: A valid symbol and orders with diverse prices
    OrderSymbol symbol = CRES;
    orderNatsApi.createOrder(new CreateOrderRequest(valueOf(500), symbol, BUY));
    orderNatsApi.createOrder(new CreateOrderRequest(valueOf(600), YPFD, BUY));
    orderNatsApi.createOrder(new CreateOrderRequest(valueOf(100), symbol, SELL));
    orderNatsApi.createOrder(new CreateOrderRequest(valueOf(110), symbol, SELL));
    String endpoint = "/orders/{symbol}/pending-bounds".replace("{symbol}", symbol.toString());
    // WHEN: finding lowest and highest pending orders
    mockMvc.perform(get(endpoint))
        .andExpect(status().isOk())
        // THEN: response should be OK 200 containing both inserted values returned
        .andExpect(jsonPath("$.lowest").value(100))
        .andExpect(jsonPath("$.highest").value(500))
        .andExpect(jsonPath("$.symbol").value(symbol.toString()));
  }

  @Test
  @SneakyThrows
  void testEmptyPendingOrderBoundEndpoint() {
    // GIVEN: A valid symbol and orders with diverse prices
    OrderSymbol symbol = IRSA;
    orderNatsApi.createOrder(new CreateOrderRequest(valueOf(30), SUPV, BUY));
    orderNatsApi.createOrder(new CreateOrderRequest(valueOf(2600), YPFD, SELL));
    String endpoint = "/orders/{symbol}/pending-bounds".replace("{symbol}", symbol.toString());
    // WHEN: finding lowest and highest pending orders
    mockMvc.perform(get(endpoint))
        .andExpect(status().isOk())
        // THEN: response should be OK 200 containing both inserted values returned
        .andExpect(jsonPath("$.lowest").value(0))
        .andExpect(jsonPath("$.highest").value(0))
        .andExpect(jsonPath("$.symbol").value(symbol.toString()));
  }

  @Test
  @SneakyThrows
  void testSymbolNotFoundOrderBoundEndpoint() {
    // GIVEN: An invalid symbol
    String symbol = "ANDRE";
    orderNatsApi.createOrder(new CreateOrderRequest(valueOf(30), SUPV, BUY));
    orderNatsApi.createOrder(new CreateOrderRequest(valueOf(2600), YPFD, SELL));
    String endpoint = "/orders/{symbol}/pending-bounds".replace("{symbol}", symbol);
    // WHEN: finding lowest and highest pending orders
    mockMvc.perform(get(endpoint))
        // THEN: 404 symbol not found should be returned
        .andExpect(status().isNotFound());
  }
}
