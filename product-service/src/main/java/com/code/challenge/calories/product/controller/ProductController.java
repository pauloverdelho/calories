package com.code.challenge.calories.product.controller;

import com.code.challenge.calories.product.model.Product;
import com.code.challenge.calories.product.repository.ProductRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;

    @GetMapping("/{barcode}")
    public Optional<Product> getProduct(
            @PathVariable Long barcode) {

        return productRepository.findByBarcode(barcode);
    }

    @GetMapping()
    public List<Product> listProducts() {

        return productRepository.findAll();
    }

    @PostMapping
    public Product addProduct(@RequestBody @Valid Product product) {
        Product toSave = productRepository.findByBarcode(product.getBarcode())
                .map(saved -> updateProduct(saved, product))
                .orElse(product);

        return productRepository.save(toSave);
    }

    @DeleteMapping("/{barcode}")
    public void removeProduct(
            @PathVariable Long barcode) {

        productRepository.findByBarcode(barcode).ifPresent(productRepository::delete);
    }

    private Product updateProduct(Product saved, Product product) {
        saved.setCalories(product.getCalories());
        return saved;
    }
}
