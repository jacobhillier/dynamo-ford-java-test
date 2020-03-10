package com.github.jacobhillier.dynamoford.model;

import java.math.BigDecimal;

public class CalculatedBasket {

    private final BigDecimal total;

    public CalculatedBasket(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getTotal() {
        return total;
    }
}
