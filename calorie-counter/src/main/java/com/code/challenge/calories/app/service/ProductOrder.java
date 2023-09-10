package com.code.challenge.calories.app.service;

import com.code.challenge.calories.app.client.product.entity.Product;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductOrder {

    private Long id;

    private String user;

    @NotNull
    private Product mainCourse;

    @NotNull
    private Product beverage;

    private Integer calories;

    public int getCalories() {
        if (calories == null) {
            calories = Optional.ofNullable(mainCourse).map(Product::getCalories).orElse(0) +
                    Optional.ofNullable(beverage).map(Product::getCalories).orElse(0);
        }
        return calories;
    }

    public void setMainCourse(Product mainCourse) {
        this.mainCourse = mainCourse;
        this.calories = null;
    }

    public void setBeverage(Product beverage) {
        this.beverage = beverage;
        this.calories = null;
    }
}
