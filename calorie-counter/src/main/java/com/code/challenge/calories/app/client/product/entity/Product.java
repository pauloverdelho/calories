package com.code.challenge.calories.app.client.product.entity;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    private Long id;

    @PositiveOrZero
    private int calories;

    @NotNull
    @Length(min = 1, max = 1000)
    private String name;

    private ProductType type;
}
