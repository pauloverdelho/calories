package com.code.challenge.calories.order.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "user_order")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
