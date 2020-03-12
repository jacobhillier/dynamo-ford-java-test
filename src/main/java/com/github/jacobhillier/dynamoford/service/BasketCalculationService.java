package com.github.jacobhillier.dynamoford.service;

import com.github.jacobhillier.dynamoford.data.DiscountRepository;
import com.github.jacobhillier.dynamoford.data.ProductRepository;
import com.github.jacobhillier.dynamoford.model.BasketItem;
import com.github.jacobhillier.dynamoford.model.Discount;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@RequiredArgsConstructor
public class BasketCalculationService {

    private final ProductRepository productRepository;
    private final DiscountRepository discountRepository;

    public BigDecimal calculateBasketValue(List<BasketItem> basketItems, LocalDateTime dateTime) {
        List<String> basketProductNames = extractBasketProductNames(basketItems);

        Map<String, Discount> productNameToDiscount = findAndMapProductDiscounts(dateTime, basketProductNames);

        return basketItems.stream()
                .map(basketItem -> {
                    Discount discount = productNameToDiscount.get(basketItem.getProductName());
                    return calculateBasketTotalForProduct(basketItem, discount);
                })
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    private List<String> extractBasketProductNames(List<BasketItem> basketItems) {
        return basketItems.stream().map(BasketItem::getProductName).collect(toList());
    }

    private Map<String, Discount> findAndMapProductDiscounts(LocalDateTime dateTime, List<String> basketProductNames) {
        return discountRepository.findValidDiscounts(basketProductNames, dateTime).stream()
                .collect(toMap(Discount::getProductName, Function.identity()));
    }

    private BigDecimal calculateBasketTotalForProduct(BasketItem basketItem, Discount discount) {
        BigDecimal productPrice = productRepository.findProductPrice(basketItem.getProductName());
        BigDecimal priceWithoutDiscount = productPrice.multiply(BigDecimal.valueOf(basketItem.getQuantity()));

        if (discount == null) {
            return priceWithoutDiscount;
        } else {
            return priceWithoutDiscount.multiply(discount.getMultiplier());
        }
    }
}
