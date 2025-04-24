package me.andre.orderintake.config;

import java.math.BigDecimal;
import java.util.HashMap;
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
  public HashMap<OrderSymbol, HashMap<OrderType, TreeMap<BigDecimal, LinkedHashSet<UUID>>>> pendingMemory() {
    HashMap<OrderSymbol, HashMap<OrderType, TreeMap<BigDecimal, LinkedHashSet<UUID>>>> memory = new HashMap<>();
    for (OrderSymbol symbol : OrderSymbol.values()) {
      memory.put(symbol, createOrderByType());
    }
    return memory;
  }

  @Bean
  public HashMap<OrderSymbol, HashSet<UUID>> matchedMemory() {
    HashMap<OrderSymbol, HashSet<UUID>> memory = new HashMap<>(OrderSymbol.values().length);
    for (OrderSymbol symbol : OrderSymbol.values()) {
      memory.put(symbol, new HashSet<>());
    }
    return memory;
  }

  // TODO: WeakHashMap / ConcurrentHashMap
  @Bean
  public ConcurrentHashMap<String, ReentrantLock> memoryLocks() {
    return new ConcurrentHashMap<>();
  }

  private HashMap<OrderType, TreeMap<BigDecimal, LinkedHashSet<UUID>>> createOrderByType() {
    HashMap<OrderType, TreeMap<BigDecimal, LinkedHashSet<UUID>>> ordersByType = new HashMap<>();
    for (OrderType type : OrderType.values()) {
      ordersByType.put(type, new TreeMap<>());
    }
    return ordersByType;
  }
}