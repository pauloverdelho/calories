package com.code.challenge.calories.app.client.order;

import com.code.challenge.calories.app.client.order.entity.Order;
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
public class OrderClientImpl implements OrderClient {
    public static final ParameterizedTypeReference<List<Order>> ORDER_LIST_TYPE = new ParameterizedTypeReference<>() {
    };

    private final RestTemplate restTemplate = new RestTemplate();
    private final OrderProperties orderProperties;

    @Override
    public Optional<Order> getOrder(Long id) {
        Order response = restTemplate.getForObject(orderProperties.getBaseUrl() + "/{id}", Order.class, id);
        return Optional.ofNullable(response);
    }

    @Override
    public List<Order> listOrders() {
        String urlTemplate = UriComponentsBuilder.fromHttpUrl(orderProperties.getBaseUrl())
                .encode()
                .toUriString();

        return restTemplate.exchange(urlTemplate, HttpMethod.GET, null, ORDER_LIST_TYPE).getBody();
    }

    @Override
    public Order addOrder(Order order) {

        return restTemplate.postForObject(orderProperties.getBaseUrl(), order, Order.class);
    }

    @Override
    public void removeOrder(Long id) {
        restTemplate.delete(orderProperties.getBaseUrl() + "/{id}", id);
    }
}
