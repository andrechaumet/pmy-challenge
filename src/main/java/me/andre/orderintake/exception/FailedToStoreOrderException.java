package me.andre.orderintake.exception;

public class FailedToStoreOrderException extends RuntimeException {

  public static final FailedToStoreOrderException instance = new FailedToStoreOrderException();
}
