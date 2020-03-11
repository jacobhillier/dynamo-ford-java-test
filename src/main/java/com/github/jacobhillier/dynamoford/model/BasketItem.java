package com.github.jacobhillier.dynamoford.model;

import lombok.Data;

@Data
public class BasketItem {

    private final String productName;
    private final int quantity;
}
