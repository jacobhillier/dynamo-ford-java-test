package com.github.jacobhillier.dynamoford.model;

import java.util.Objects;

public class BasketItem {

    private final String productName;
    private final int quantity;

    public BasketItem(String productName, int quantity) {
        this.productName = productName;
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BasketItem that = (BasketItem) o;
        return quantity == that.quantity && Objects.equals(productName, that.productName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productName, quantity);
    }
}
