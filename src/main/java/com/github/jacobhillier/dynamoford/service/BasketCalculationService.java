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
        Map<String, BasketItem> productNameToBasketItem = mapProductNameToBasketItem(basketItems);

        Map<String, Discount> productNameToDiscount = findAndMapProductDiscountsToProductName(dateTime, productNameToBasketItem.keySet());

        return basketItems.stream()
                .map(basketItem -> {
                    Discount discount = productNameToDiscount.get(basketItem.getProductName());
                    return calculateBasketTotalForProduct(basketItem, discount, productNameToBasketItem);
                })
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    private Map<String, BasketItem> mapProductNameToBasketItem(List<BasketItem> basketItems) {
        return basketItems.stream()
                .collect(toMap(BasketItem::getProductName, Function.identity()));
    }

    private Map<String, Discount> findAndMapProductDiscountsToProductName(LocalDateTime dateTime, Set<String> basketProductNames) {
        return discountRepository.findValidDiscounts(basketProductNames, dateTime).stream()
                .collect(toMap(Discount::getProductName, Function.identity()));
    }

    private BigDecimal calculateBasketTotalForProduct(BasketItem basketItem, Discount discount, Map<String, BasketItem> productNameToBasketItem) {
        BigDecimal productPrice = productRepository.findProductPrice(basketItem.getProductName());

        if (discount == null || !basketIsEligibleForDiscount(productNameToBasketItem, discount)) {
            return calculatePriceWithoutDiscount(basketItem, productPrice);
        } else {
            return calculatePriceWithDiscount(basketItem, productPrice, discount);
        }
    }

    private boolean basketIsEligibleForDiscount(Map<String, BasketItem> productNameToBasketItem, Discount discount) {
        RequiredBasketItem requiredBasketItem = discount.getRequiredBasketItem();
        if (requiredBasketItem == null) {
            return true;
        } else {
            BasketItem basketItem = productNameToBasketItem.get(requiredBasketItem.getProductName());
            return basketItem != null && basketItem.getQuantity() >= requiredBasketItem.getMinimumQuantity();
        }
    }

    private BigDecimal calculatePriceWithoutDiscount(BasketItem basketItem, BigDecimal productPrice) {
        return productPrice.multiply(BigDecimal.valueOf(basketItem.getQuantity()));
    }

    private BigDecimal calculatePriceWithDiscount(BasketItem basketItem, BigDecimal productPrice, Discount discount) {
        int quantityWithDiscount = quantityEligibleForDiscount(basketItem, discount);
        int quantityWithoutDiscount = basketItem.getQuantity() - quantityWithDiscount;

        BigDecimal quantityWithDiscountPrice = productPrice
                .multiply(BigDecimal.valueOf(quantityWithDiscount))
                .multiply(discount.getMultiplier());

        BigDecimal quantityWithoutDiscountPrice = productPrice
                .multiply(BigDecimal.valueOf(quantityWithoutDiscount));

        return quantityWithoutDiscountPrice.add(quantityWithDiscountPrice);
    }

    private int quantityEligibleForDiscount(BasketItem basketItem, Discount discount) {
        Integer discountQuantity = discount.getDiscountQuantity();
        if (discountQuantity == null || discountQuantity >= basketItem.getQuantity()) {
            return basketItem.getQuantity();
        } else {
            return discountQuantity;
        }
    }
}
