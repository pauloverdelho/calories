package com.code.challenge.calories.app.client.order.entity;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    private Long id;

    @NotNull
    @Length(min = 1, max = 1000)
    private String user;

    @NotNull
    @Length(min = 1, max = 1000)
    private String mainCourse;

    @NotNull
    @Length(min = 1, max = 1000)
    private String beverage;

    @Positive
    private int calories;
}
