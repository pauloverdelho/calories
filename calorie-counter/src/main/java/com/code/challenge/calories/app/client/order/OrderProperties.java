package com.code.challenge.calories.app.client.order;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("order-client")
@Data
public class OrderProperties {

    private String baseUrl;
}
