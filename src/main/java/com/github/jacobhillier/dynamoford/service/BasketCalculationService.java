package com.github.jacobhillier.dynamoford.service;

import com.github.jacobhillier.dynamoford.data.ProductRepository;
import com.github.jacobhillier.dynamoford.model.BasketItem;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class BasketCalculationService {

    private final ProductRepository productRepository;

    public BigDecimal calculateBasketValue(List<BasketItem> basketItems, LocalDateTime dateTime) {
        return basketItems.stream()
                .map(this::calculateBasketTotalForProduct)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    private BigDecimal calculateBasketTotalForProduct(BasketItem basketItem) {
        BigDecimal productPrice = productRepository.findProductPrice(basketItem.getProductName());
        return productPrice.multiply(BigDecimal.valueOf(basketItem.getQuantity()));
    }
}
