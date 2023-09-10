package com.code.challenge.calories.app.client.product;

import com.code.challenge.calories.app.client.order.entity.Order;
import com.code.challenge.calories.app.client.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductClientImpl implements ProductClient {
    public static final ParameterizedTypeReference<List<Product>> PRODUCT_LIST_TYPE = new ParameterizedTypeReference<>() {
    };

    private final RestTemplate restTemplate = new RestTemplate();
    private final ProductProperties productProperties;

    @Override
    public Optional<Product> getProduct(Long id) {
        Product response = restTemplate.getForObject(productProperties.getBaseUrl() + "/{id}", Product.class, id);
        return Optional.ofNullable(response);
    }

    @Override
    public List<Product> listProducts() {
        String urlTemplate = UriComponentsBuilder.fromHttpUrl(productProperties.getBaseUrl())
                .encode()
                .toUriString();

        return restTemplate.exchange(urlTemplate, HttpMethod.GET, null, PRODUCT_LIST_TYPE).getBody();
    }

    @Override
    public Product addProduct(Product product) {
        return restTemplate.postForObject(productProperties.getBaseUrl(), product, Product.class);
    }

    @Override
    public void removeProduct(Long id) {
        restTemplate.delete(productProperties.getBaseUrl() + "/{id}", id);
    }
}
