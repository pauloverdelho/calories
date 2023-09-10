package com.code.challenge.calories.app.service;

import com.code.challenge.calories.app.client.product.ProductClient;
import com.code.challenge.calories.app.client.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductClient productClient;

    public Optional<Product> getProduct(Long id) {
        return productClient.getProduct(id);
    }

    public List<Product> listProducts() {
        return productClient.listProducts();
    }

    public Product addProduct(Product product) {
        return productClient.addProduct(product);
    }

    public void removeProduct(Long id) {
        productClient.removeProduct(id);
    }
}
