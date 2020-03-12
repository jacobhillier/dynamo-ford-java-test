package com.github.jacobhillier.dynamoford.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Discount {

    private final String productName;
    private final BigDecimal multiplier;
    private final RequiredBasketItem requiredBasketItem;
    private final Integer discountQuantity;
    private final LocalDateTime validFrom;
    private final LocalDateTime validTo;
}
