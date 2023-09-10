package com.code.challenge.calories.app.client.order;

import com.code.challenge.calories.app.client.order.entity.Order;

import java.util.List;
import java.util.Optional;

public interface OrderClient {
    Optional<Order> getOrder(Long id);

    List<Order> listOrders();

    Order addOrder(Order order);

    void removeOrder(Long id);
}
