package com.github.jacobhillier.dynamoford.model;

import lombok.Data;

@Data
public class RequiredBasketItem {

    private final String productName;
    private final int minimumQuantity;
}
