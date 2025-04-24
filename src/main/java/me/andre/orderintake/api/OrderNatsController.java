package me.andre.orderintake.api;

import static java.util.UUID.randomUUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.andre.orderintake.business.OrderMatchService;
import me.andre.orderintake.models.domains.Order;
import me.andre.orderintake.models.dtos.CreateOrderRequest;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class OrderNatsController implements OrderNatsApi {

  private final OrderMatchService orderMatchService;

  // TODO: NATS Consumer to invoke

  @Override
  public void createOrder(CreateOrderRequest request) {
    orderMatchService.match(register(request));
  }

  private Order register(CreateOrderRequest request) {
    return new Order(randomUUID(), request.price(), request.symbol(), request.type());
  }
}
