package com.code.challenge.calories.app.client.product;

import com.code.challenge.calories.app.client.product.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductClient {
    Optional<Product> getProduct(Long id);

    List<Product> listProducts();

    Product addProduct(Product product);

    void removeProduct(Long id);
}
