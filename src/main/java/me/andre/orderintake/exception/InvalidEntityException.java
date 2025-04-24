package me.andre.orderintake.exception;

public class InvalidEntityException extends OrderIntakeException {

  public static final InvalidEntityException instance = new InvalidEntityException();
}
