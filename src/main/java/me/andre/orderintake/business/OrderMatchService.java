package me.andre.orderintake.business;

import me.andre.orderintake.models.domains.Order;

public interface OrderMatchService {

  void match(Order order);
}
