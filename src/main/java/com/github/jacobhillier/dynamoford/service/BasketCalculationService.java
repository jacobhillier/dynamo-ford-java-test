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

        if (discount == null || !basketEligibleForDiscount(discount, productNameToBasketItem)) {
            return productPrice.multiply(BigDecimal.valueOf(basketItem.getQuantity()));
        } else {
            return calculatePriceWithDiscount(basketItem, productPrice, discount);
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

    private BigDecimal calculatePriceWithDiscount(BasketItem basketItem, BigDecimal productPrice, Discount discount) {
        int itemsWithDiscount = numberOfItemsEligibleForDiscount(basketItem, discount);
        int itemsWithoutDiscount = basketItem.getQuantity() - itemsWithDiscount;

        BigDecimal itemsWithoutDiscountPrice = productPrice
                .multiply(BigDecimal.valueOf(itemsWithoutDiscount));

        BigDecimal itemsWithDiscountPrice = productPrice
                .multiply(BigDecimal.valueOf(itemsWithDiscount))
                .multiply(discount.getMultiplier());

        return itemsWithoutDiscountPrice.add(itemsWithDiscountPrice);
    }

    private int numberOfItemsEligibleForDiscount(BasketItem basketItem, Discount discount) {
        Integer discountQuantity = discount.getDiscountQuantity();
        if (discountQuantity == null || discountQuantity >= basketItem.getQuantity()) {
            return basketItem.getQuantity();
        } else {
            return discountQuantity;
        }
    }
}
