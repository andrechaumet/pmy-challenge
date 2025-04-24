package me.andre.orderintake.exception;

public class LockTimeoutException extends OrderIntakeException {

  public static final LockTimeoutException instance = new LockTimeoutException();
}
