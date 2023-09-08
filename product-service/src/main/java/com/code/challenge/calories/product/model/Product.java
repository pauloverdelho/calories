package com.code.challenge.calories.product.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Entity
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Positive
    private long calories;

    @Column(unique = true)
    @NotNull
    @Length(min = 1, max = 1000)
    private String name;

    @Enumerated(EnumType.STRING)
    private ProductType type;
}
