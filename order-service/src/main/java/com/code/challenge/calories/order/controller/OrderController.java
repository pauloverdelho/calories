package com.code.challenge.calories.order.controller;

import com.code.challenge.calories.order.model.Order;
import com.code.challenge.calories.order.repository.OrderRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository orderRepository;

    @GetMapping("/{id}")
    public Optional<Order> getOrder(
            @PathVariable Long id) {

        return orderRepository.findById(id);
    }

    @GetMapping()
    public List<Order> listOrders(
            @RequestParam(required = false) String user) {

        if (user == null) {
            return orderRepository.findAll();
        }
        return orderRepository.findAllByUser(user);
    }

    @PostMapping
    public Order addOrder(@RequestBody @Valid Order order) {
        //if order being added has invalid id, clear id and add order
        if (order.getId() != null && getOrder(order.getId()).isEmpty()) {
            order.setId(null);
        }

        return orderRepository.save(order);
    }

    @DeleteMapping("/{id}")
    public void removeOrder(
            @PathVariable Long id) {

        orderRepository.deleteById(id);
    }
}
