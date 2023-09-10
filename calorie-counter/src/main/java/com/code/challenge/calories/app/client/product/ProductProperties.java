package com.code.challenge.calories.app.client.product;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("product-client")
@Data
public class ProductProperties {

    public String baseUrl;
}
