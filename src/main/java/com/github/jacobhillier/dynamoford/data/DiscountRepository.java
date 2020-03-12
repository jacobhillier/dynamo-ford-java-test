package com.github.jacobhillier.dynamoford.data;

import com.github.jacobhillier.dynamoford.model.Discount;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class DiscountRepository {

    public List<Discount> findValidDiscounts(Set<String> productNames, LocalDateTime dateTime) {
        return null;
    }
}
