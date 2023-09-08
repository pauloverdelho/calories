package com.code.challenge.calories.product.controller;

import com.code.challenge.calories.product.model.Product;
import com.code.challenge.calories.product.repository.ProductRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;

    @GetMapping("/{id}")
    public Optional<Product> getProduct(
            @PathVariable Long id) {

        return productRepository.findById(id);
    }

    @GetMapping()
    public List<Product> listProducts() {

        return productRepository.findAll();
    }

    @PostMapping
    public Product addProduct(@RequestBody @Valid Product product) {
        //if product being added has invalid id, clear id and add product
        if (product.getId() != null && getProduct(product.getId()).isEmpty()) {
            product.setId(null);
        }

        return productRepository.save(product);
    }

    @DeleteMapping("/{id}")
    public void removeProduct(
            @PathVariable Long id) {

        productRepository.deleteById(id);
    }
}
