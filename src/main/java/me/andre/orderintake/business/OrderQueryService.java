package me.andre.orderintake.business;

import java.math.BigDecimal;
import me.andre.orderintake.models.enums.OrderSymbol;
import org.apache.commons.lang3.tuple.Pair;

public interface OrderQueryService {

  Integer findExecutedCountBySymbol(OrderSymbol orderSymbol);

  Pair<BigDecimal, BigDecimal> findOrderBounds(OrderSymbol symbol);
}
