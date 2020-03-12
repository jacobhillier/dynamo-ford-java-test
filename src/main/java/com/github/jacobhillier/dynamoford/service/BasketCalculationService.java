package com.github.jacobhillier.dynamoford.service;

import com.github.jacobhillier.dynamoford.data.DiscountRepository;
import com.github.jacobhillier.dynamoford.data.ProductRepository;
import com.github.jacobhillier.dynamoford.model.BasketItem;
import com.github.jacobhillier.dynamoford.model.Discount;
import com.github.jacobhillier.dynamoford.model.RequiredBasketItem;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@RequiredArgsConstructor
public class BasketCalculationService {

    private final ProductRepository productRepository;
    private final DiscountRepository discountRepository;

    public BigDecimal calculateBasketValue(List<BasketItem> basketItems, LocalDateTime dateTime) {
        Map<String, BasketItem> productNameToBasketItem = extractBasketProductNames(basketItems);

        Map<String, Discount> productNameToDiscount = findAndMapProductDiscounts(dateTime, productNameToBasketItem.keySet());

        return basketItems.stream()
                .map(basketItem -> {
                    Discount discount = productNameToDiscount.get(basketItem.getProductName());
                    return calculateBasketTotalForProduct(basketItem, discount, productNameToBasketItem);
                })
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    private Map<String, BasketItem> extractBasketProductNames(List<BasketItem> basketItems) {
        return basketItems.stream()
                .collect(toMap(BasketItem::getProductName, Function.identity()));
    }

    private Map<String, Discount> findAndMapProductDiscounts(LocalDateTime dateTime, Set<String> basketProductNames) {
        return discountRepository.findValidDiscounts(basketProductNames, dateTime).stream()
                .collect(toMap(Discount::getProductName, Function.identity()));
    }

    private BigDecimal calculateBasketTotalForProduct(BasketItem basketItem, Discount discount, Map<String, BasketItem> productNameToBasketItem) {
        BigDecimal productPrice = productRepository.findProductPrice(basketItem.getProductName());
        BigDecimal priceWithoutDiscount = productPrice.multiply(BigDecimal.valueOf(basketItem.getQuantity()));

        if (discount == null || !basketEligibleForDiscount(discount, productNameToBasketItem)) {
            return priceWithoutDiscount;
        } else {
            return priceWithoutDiscount.multiply(discount.getMultiplier());
        }
    }

    private boolean basketEligibleForDiscount(Discount discount, Map<String, BasketItem> productNameToBasketItem) {
        RequiredBasketItem requiredBasketItem = discount.getRequiredBasketItem();
        if (requiredBasketItem == null) {
            return true;
        } else {
            BasketItem basketItem = productNameToBasketItem.get(requiredBasketItem.getProductName());
            return basketItem != null && basketItem.getQuantity() >= requiredBasketItem.getMinimumQuantity();
        }
    }
}
