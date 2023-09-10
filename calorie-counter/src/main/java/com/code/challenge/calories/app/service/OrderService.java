package com.code.challenge.calories.app.service;

import com.code.challenge.calories.app.client.order.OrderClient;
import com.code.challenge.calories.app.client.order.entity.Order;
import com.code.challenge.calories.app.client.product.entity.Product;
import com.code.challenge.calories.app.client.product.entity.ProductType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderClient orderClient;
    private final ProductService productService;

    public Optional<ProductOrder> getOrder(Long id) {
        return orderClient.getOrder(id).map(this::getProductOrder);
    }

    public List<Order> listOrders() {
        return orderClient.listOrders();
    }

    public Order addOrder(ProductOrder productOrder) {
        return orderClient.addOrder(getProductOrder(productOrder));
    }

    public void removeOrder(Long id) {
        orderClient.removeOrder(id);
    }

    private ProductOrder getProductOrder(Order order) {
        List<Product> products = productService.listProducts();

        Product mainCourse = products.stream()
                .filter(product -> product.getType() == ProductType.MAIN_COURSE)
                .filter(product -> product.getName().equals(order.getMainCourse()))
                .findFirst().orElseGet(Product::new);

        Product beverage = products.stream()
                .filter(product -> product.getType() == ProductType.BEVERAGE)
                .filter(product -> product.getName().equals(order.getBeverage()))
                .findFirst().orElseGet(Product::new);

        return new ProductOrder(order.getId(), order.getUser(), mainCourse, beverage, order.getCalories());
    }

    private static Order getProductOrder(ProductOrder productOrder) {
        return new Order(productOrder.getId(),
                productOrder.getUser(),
                productOrder.getMainCourse().getName(),
                productOrder.getBeverage().getName(),
                productOrder.getCalories());
    }
}
