package me.andre.orderintake.api;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.andre.orderintake.business.OrderQueryService;
import me.andre.orderintake.exception.SymbolNotFoundException;
import me.andre.orderintake.models.dtos.ExecutedCountResponse;
import me.andre.orderintake.models.dtos.PendingBoundsResponse;
import me.andre.orderintake.models.enums.OrderSymbol;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
public class OrderRestController implements OrderRestApi {

  private final OrderQueryService orderQueryService;

  @Override
  public ResponseEntity<ExecutedCountResponse> getExecutedOrders(String symbolPath) {
    OrderSymbol symbol = getSymbolOrThrow(symbolPath);
    Integer countBySymbol = orderQueryService.findExecutedCountBySymbol(symbol);
    return ResponseEntity.ok(new ExecutedCountResponse(symbol, countBySymbol));
  }

  @Override
  public ResponseEntity<PendingBoundsResponse> getOrderBounds(String symbolPath) {
    OrderSymbol symbol = getSymbolOrThrow(symbolPath);
    Pair<BigDecimal, BigDecimal> bounds = orderQueryService.findOrderBounds(symbol);
    return ResponseEntity.ok(
        new PendingBoundsResponse(symbol, bounds.getLeft(), bounds.getRight()));
  }

  private OrderSymbol getSymbolOrThrow(String symbol) {
    try {
      return OrderSymbol.valueOf(symbol.toUpperCase());
    } catch (Exception e) {
      throw SymbolNotFoundException.instance;
    }
  }
}