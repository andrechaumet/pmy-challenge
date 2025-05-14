package me.andre.orderintake;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.andre.orderintake.models.dtos.ExecutedCountResponse;
import me.andre.orderintake.models.dtos.PendingBoundsResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/orders")
@Tag(name = "Orders", description = "Query operations related to orders")
public interface OrderRestApi {

  @Operation(
      summary = "Get executed order count",
      description = "Returns executed orders count filtered by symbol")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Successful retrieval"),
      @ApiResponse(responseCode = "404", description = "Symbol not found"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @GetMapping("/{symbol}/executed-count")
  ResponseEntity<ExecutedCountResponse> getExecutedOrders(@PathVariable(name = "symbol") String symbol);

  @Operation(
      summary = "Get pending order bounds",
      description = "Returns lowest and highest pending order filtered by symbol")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Successful retrieval"),
      @ApiResponse(responseCode = "404", description = "Symbol not found"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @GetMapping("/{symbol}/pending-bounds")
  ResponseEntity<PendingBoundsResponse> getOrderBounds(@PathVariable(name = "symbol") String symbol);
}
