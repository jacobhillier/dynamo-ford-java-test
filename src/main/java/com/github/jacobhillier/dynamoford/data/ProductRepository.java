package com.github.jacobhillier.dynamoford.data;

import java.math.BigDecimal;
import java.util.Map;

public class ProductRepository {

    private static final Map<String, BigDecimal> PRODUCT_PRICES = Map.of(
            "soup", BigDecimal.valueOf(0.65),
            "bread", BigDecimal.valueOf(0.80),
            "milk", BigDecimal.valueOf(1.30),
            "apples", BigDecimal.valueOf(0.10)
    );

    public BigDecimal findProductPrice(String productName) {
        return PRODUCT_PRICES.get(productName);
    }
}
