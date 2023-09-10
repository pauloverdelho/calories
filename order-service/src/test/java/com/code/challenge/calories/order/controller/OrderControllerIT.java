package com.code.challenge.calories.order.controller;

import com.code.challenge.calories.order.model.Order;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderControllerIT {

    public static final ParameterizedTypeReference<List<Order>> ORDER_LIST_TYPE = new ParameterizedTypeReference<>() {
    };
    @Container
    public static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:5.7"))
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .waitingFor(Wait.forListeningPort())
            .withEnv("MYSQL_ROOT_HOST", "%")
            .withPrivilegedMode(true);

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    private static final String TEST_USER = "paulo";

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    public void testRetrieveOrderNotExist() {

        Order response = restTemplate.getForObject("http://localhost:" + port + "/orders/1", Order.class);
        assertThat(response).isNull();

    }

    @Test
    @org.junit.jupiter.api.Order(2)
    public void testCreateOrder() {
        Order order = new Order(null, TEST_USER, "Cheeseburger", "Coca-cola", 600);

        Order response = restTemplate.postForObject("http://localhost:" + port + "/orders", order, Order.class);
        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    public void testRetrieveOrder1() {

        Order response = restTemplate.getForObject("http://localhost:" + port + "/orders/1", Order.class);
        assertThat(response).isNotNull();
        assertThat(response.getUser()).isEqualTo(TEST_USER);
    }

    @Test
    @org.junit.jupiter.api.Order(4)
    public void testCreateOrder2() {
        Order order = new Order(null, TEST_USER, "Doritos", "Water", 100);

        Order response = restTemplate.postForObject("http://localhost:" + port + "/orders", order, Order.class);
        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();
    }

    @Test
    @org.junit.jupiter.api.Order(5)
    public void testUpdateOrder() {
        Order saved = restTemplate.getForObject("http://localhost:" + port + "/orders/2", Order.class);
        assertThat(saved.getBeverage()).isEqualTo("Water");

        String sparklingWater = "Sparkling Water";
        saved.setBeverage(sparklingWater);

        Order response = restTemplate.postForObject("http://localhost:" + port + "/orders", saved, Order.class);
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(saved.getId());
        assertThat(response.getBeverage()).isEqualTo(sparklingWater);
    }

    @Test
    @org.junit.jupiter.api.Order(6)
    public void testUpdateOrderWrongId() {
        Order order = new Order(99L, TEST_USER, "Salad", "Red wine", 200);

        Order response = restTemplate.postForObject("http://localhost:" + port + "/orders", order, Order.class);
        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();
        assertThat(response.getId()).isNotEqualTo(order.getId());
    }

    @Test
    @org.junit.jupiter.api.Order(7)
    public void testRetrieveUserOrders() {

        List<Order> response = getTestOrders(TEST_USER);
        assertThat(response).isNotNull();
        assertThat(response.size()).isEqualTo(3);
    }

    @Test
    @org.junit.jupiter.api.Order(8)
    public void testRetrieveOrders() {

        List<Order> response = getTestOrders(null);
        assertThat(response).isNotNull();
        assertThat(response.size()).isEqualTo(3);
    }

    @Test
    @org.junit.jupiter.api.Order(9)
    public void testDeleteAll() {

        restTemplate.delete("http://localhost:" + port + "/orders/1");

        List<Order> response = getTestOrders(null);
        assertThat(response).isNotNull();
        assertThat(response.size()).isEqualTo(2);

        restTemplate.delete("http://localhost:" + port + "/orders/2");

        response = getTestOrders(null);
        assertThat(response).isNotNull();
        assertThat(response.size()).isEqualTo(1);

        restTemplate.delete("http://localhost:" + port + "/orders/3");

        response = getTestOrders(null);
        assertThat(response).isNotNull();
        assertThat(response).isEmpty();
    }

    private List<Order> getTestOrders(String user) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + "/orders");
        if (user != null) {
            uriComponentsBuilder = uriComponentsBuilder.queryParam("user", user);
        }
        String urlTemplate = uriComponentsBuilder.encode().toUriString();

        return restTemplate.exchange(urlTemplate, HttpMethod.GET, null, ORDER_LIST_TYPE).getBody();
    }
}