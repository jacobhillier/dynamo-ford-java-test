package com.github.jacobhillier.dynamoford.data;

import com.github.jacobhillier.dynamoford.model.Discount;
import com.github.jacobhillier.dynamoford.model.RequiredBasketItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DiscountRepository {

    private static final List<Discount> DISCOUNTS = List.of(
            new Discount(
                    "bread",
                    BigDecimal.valueOf(0.5),
                    new RequiredBasketItem("soup", 2),
                    1,
                    LocalDateTime.of(2020, 3, 11, 0, 0, 0),
                    LocalDateTime.of(2020, 3, 17, 23, 59, 59)
            )
    );

    public List<Discount> findValidDiscounts(Set<String> productNames, LocalDateTime dateTime) {
        return DISCOUNTS.stream()
                .filter(discount ->
                        productNames.contains(discount.getProductName())
                                && (discount.getValidFrom().isEqual(dateTime) || discount.getValidFrom().isBefore(dateTime))
                                && (discount.getValidTo().isEqual(dateTime) || discount.getValidTo().isAfter(dateTime))
                )
                .collect(Collectors.toList());
    }
}
