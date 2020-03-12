package com.github.jacobhillier.dynamoford.service

import com.github.jacobhillier.dynamoford.data.DiscountRepository
import com.github.jacobhillier.dynamoford.data.ProductRepository
import com.github.jacobhillier.dynamoford.model.BasketItem
import com.github.jacobhillier.dynamoford.model.Discount
import spock.lang.Specification

import java.time.LocalDateTime

class BasketCalculationServiceTest extends Specification {

    ProductRepository productRepository = Mock(ProductRepository)
    DiscountRepository discountRepository = Mock(DiscountRepository)
    BasketCalculationService basketCalculationService = new BasketCalculationService(productRepository, discountRepository)

    void "should calculate basket value with matching discounts"() {
        given:
        BasketItem basketItem1 = new BasketItem("productName1", 1)
        BigDecimal productPrice1 = 1.2

        and:
        BasketItem basketItem2 = new BasketItem("productName2", 2)
        BigDecimal productPrice2 = 3.4

        and:
        Discount discount1 = new Discount(basketItem1.productName, 0.1)
        Discount discount2 = new Discount(basketItem2.productName, 0.2)

        and:
        BigDecimal expectedProduct1PriceWithDiscount = (productPrice1 * basketItem1.quantity) * discount1.multiplier
        BigDecimal expectedProduct2PriceWithDiscount = (productPrice2 * basketItem2.quantity) * discount2.multiplier

        and:
        List<BasketItem> basketItems = [basketItem1, basketItem2]
        LocalDateTime dateTime = LocalDateTime.now()

        when:
        BigDecimal basketValue = basketCalculationService.calculateBasketValue(basketItems, dateTime)

        then:
        basketValue == expectedProduct1PriceWithDiscount + expectedProduct2PriceWithDiscount

        and:
        1 * productRepository.findProductPrice(basketItem1.productName) >> productPrice1
        1 * productRepository.findProductPrice(basketItem2.productName) >> productPrice2
        1 * discountRepository.findValidDiscounts([basketItem1.productName, basketItem2.productName], dateTime) >> [discount1, discount2]
    }

    void "should calculate basket value with no matching discounts"() {
        given:
        BasketItem basketItem1 = new BasketItem("productName1", 1)
        BigDecimal productPrice1 = 1.2

        and:
        BigDecimal expectedProduct1Price = (productPrice1 * basketItem1.quantity)

        when:
        BigDecimal basketValue = basketCalculationService.calculateBasketValue([basketItem1], LocalDateTime.now())

        then:
        basketValue == expectedProduct1Price

        and:
        1 * productRepository.findProductPrice(_) >> productPrice1
        1 * discountRepository.findValidDiscounts(_, _) >> []
    }
}
