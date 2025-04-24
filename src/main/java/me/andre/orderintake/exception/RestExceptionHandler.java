package me.andre.orderintake.exception;

import lombok.extern.slf4j.Slf4j;
import me.andre.orderintake.models.dtos.RestErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<RestErrorResponse> handleGenericException(Exception e) {
    log.error("Generic error: {}", e.getMessage());
    return ResponseEntity.internalServerError().build();
  }

  @ExceptionHandler(SymbolNotFoundException.class)
  public ResponseEntity<String> handleSymbolNotFound(SymbolNotFoundException ex) {
    return ResponseEntity.notFound().build();
  }
}
