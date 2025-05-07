package me.andre.orderintake.config;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import me.andre.orderintake.models.enums.OrderSymbol;
import me.andre.orderintake.models.enums.OrderType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MemoryConfig {

  @Bean
  public EnumMap<OrderSymbol, EnumMap<OrderType, TreeMap<BigDecimal, LinkedHashSet<UUID>>>> pendingMemory() {
    EnumMap<OrderSymbol, EnumMap<OrderType, TreeMap<BigDecimal, LinkedHashSet<UUID>>>> memory = new EnumMap<>(OrderSymbol.class);
    for (OrderSymbol symbol : OrderSymbol.values()) {
      memory.put(symbol, createOrderByType());
    }
    return memory;
  }

  @Bean
  public EnumMap<OrderSymbol, HashSet<UUID>> matchedMemory() {
    EnumMap<OrderSymbol, HashSet<UUID>> memory = new EnumMap<>(OrderSymbol.class);
    for (OrderSymbol symbol : OrderSymbol.values()) {
      memory.put(symbol, new HashSet<>());
    }
    return memory;
  }

  @Bean
  public ConcurrentHashMap<String, ReentrantLock> locksMemory() {
    return new ConcurrentHashMap<>();
  }

  private EnumMap<OrderType, TreeMap<BigDecimal, LinkedHashSet<UUID>>> createOrderByType() {
    EnumMap<OrderType, TreeMap<BigDecimal, LinkedHashSet<UUID>>> ordersByType = new EnumMap<>(OrderType.class);
    for (OrderType type : OrderType.values()) {
      ordersByType.put(type, new TreeMap<>());
    }
    return ordersByType;
  }
}