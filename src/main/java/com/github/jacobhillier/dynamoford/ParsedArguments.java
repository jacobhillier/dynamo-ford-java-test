package com.github.jacobhillier.dynamoford;

import com.github.jacobhillier.dynamoford.model.BasketItem;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class ParsedArguments {

    private final List<BasketItem> basketItems;
    private final LocalDateTime dateTime;

    public ParsedArguments(List<BasketItem> basketItems, LocalDateTime dateTime) {
        this.basketItems = basketItems;
        this.dateTime = dateTime;
    }

    public List<BasketItem> getBasketItems() {
        return basketItems;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParsedArguments that = (ParsedArguments) o;
        return Objects.equals(basketItems, that.basketItems) && Objects.equals(dateTime, that.dateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(basketItems, dateTime);
    }
}
