package me.andre.orderintake.utils;

import static java.util.stream.Collectors.joining;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import me.andre.orderintake.exception.InvalidEntityException;

@Slf4j
@UtilityClass
public class JakartaUtils {

  private static final Validator validator;

  static {
    try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
      validator = factory.getValidator();
    }
  }

  public static <T> void valid(T object) {
    Set<ConstraintViolation<T>> violations = validate(object);
    if (!violations.isEmpty()) {
      logViolations(violations);
      throw InvalidEntityException.instance;
    }
  }

  private static <T> Set<ConstraintViolation<T>> validate(T object) {
    return validator.validate(object);
  }

  private static void logViolations(Set<? extends ConstraintViolation<?>> violations) {
    String errorMessages = formatViolations(violations);
    log.error("Validation failed: {}", errorMessages);
  }

  private static String formatViolations(Set<? extends ConstraintViolation<?>> violations) {
    return violations.stream()
        .map(v -> v.getPropertyPath() + ": " + v.getMessage())
        .collect(joining("; "));
  }
}