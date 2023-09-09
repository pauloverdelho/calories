package com.code.challenge.calories.product.controller;

import com.code.challenge.calories.product.model.Product;
import com.code.challenge.calories.product.model.ProductType;
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
public class ProductControllerIT {

    public static final ParameterizedTypeReference<List<Product>> ORDER_LIST_TYPE = new ParameterizedTypeReference<>() {
    };
    @Container
    public static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:5.7"))
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .waitingFor(Wait.forListeningPort())
            .withEnv("MYSQL_ROOT_HOST", "%");

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

        Product response = restTemplate.getForObject("http://localhost:" + port + "/products/1", Product.class);
        assertThat(response).isNull();

    }

    @Test
    @org.junit.jupiter.api.Order(2)
    public void testCreateOrder() {
        Product product = new Product(null, 450, "Cheeseburger", ProductType.MAIN_COURSE);

        Product response = restTemplate.postForObject("http://localhost:" + port + "/products", product, Product.class);
        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    public void testRetrieveProduct1() {

        Product response = restTemplate.getForObject("http://localhost:" + port + "/products/1", Product.class);
        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();
    }

    @Test
    @org.junit.jupiter.api.Order(4)
    public void testCreateProduct2() {
        Product product = new Product(null, 150, "Coca-cola", ProductType.BEVERAGE);

        Product response = restTemplate.postForObject("http://localhost:" + port + "/products", product, Product.class);
        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();
    }

    @Test
    @org.junit.jupiter.api.Order(5)
    public void testUpdateProduct() {
        Product saved = restTemplate.getForObject("http://localhost:" + port + "/products/2", Product.class);
        assertThat(saved.getName()).isEqualTo("Coca-cola");

        String newName = "Coca-Cola";
        saved.setName(newName);

        Product response = restTemplate.postForObject("http://localhost:" + port + "/products", saved, Product.class);
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(saved.getId());
        assertThat(response.getName()).isEqualTo(newName);
    }

    @Test
    @org.junit.jupiter.api.Order(6)
    public void testUpdateProductWrongId() {
        Product product = new Product(99L, 100, "Salad", ProductType.MAIN_COURSE);

        Product response = restTemplate.postForObject("http://localhost:" + port + "/products", product, Product.class);
        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();
        assertThat(response.getId()).isNotEqualTo(product.getId());
    }

    @Test
    @org.junit.jupiter.api.Order(7)
    public void testRetrieveProducts() {

        List<Product> response = getTestProducts();
        assertThat(response).isNotNull();
        assertThat(response.size()).isEqualTo(3);
    }

    @Test
    @org.junit.jupiter.api.Order(8)
    public void testDeleteAll() {

        restTemplate.delete("http://localhost:" + port + "/products/1");

        List<Product> response = getTestProducts();
        assertThat(response).isNotNull();
        assertThat(response.size()).isEqualTo(2);

        restTemplate.delete("http://localhost:" + port + "/products/2");

        response = getTestProducts();
        assertThat(response).isNotNull();
        assertThat(response.size()).isEqualTo(1);

        restTemplate.delete("http://localhost:" + port + "/products/3");

        response = getTestProducts();
        assertThat(response).isNotNull();
        assertThat(response).isEmpty();
    }

    private List<Product> getTestProducts() {
        String urlTemplate = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + "/products")
                .encode()
                .toUriString();

        return restTemplate.exchange(urlTemplate, HttpMethod.GET, null, ORDER_LIST_TYPE).getBody();
    }
}