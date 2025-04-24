package me.andre.orderintake.exception;

public class SymbolNotFoundException extends OrderIntakeException {

  public static final SymbolNotFoundException instance = new SymbolNotFoundException();
}
