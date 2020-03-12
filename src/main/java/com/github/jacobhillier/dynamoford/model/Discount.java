package com.github.jacobhillier.dynamoford.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Discount {

    private final String productName;
    private final BigDecimal multiplier;
    private final RequiredBasketItem requiredBasketItem;
}
