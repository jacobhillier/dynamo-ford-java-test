package com.github.jacobhillier.dynamoford.service

import com.github.jacobhillier.dynamoford.data.DiscountRepository
import com.github.jacobhillier.dynamoford.data.ProductRepository
import com.github.jacobhillier.dynamoford.model.BasketItem
import com.github.jacobhillier.dynamoford.model.Discount
import com.github.jacobhillier.dynamoford.model.RequiredBasketItem
import spock.lang.Specification

import java.time.LocalDateTime

import static java.time.LocalDateTime.MAX
import static java.time.LocalDateTime.MIN

class BasketCalculationServiceTest extends Specification {

    ProductRepository productRepository = Mock(ProductRepository)
    DiscountRepository discountRepository = Mock(DiscountRepository)
    BasketCalculationService basketCalculationService = new BasketCalculationService(productRepository, discountRepository)

    BasketItem basketItem1 = new BasketItem("productName1", 5)
    BigDecimal productPrice1 = 1.2
    BasketItem basketItem2 = new BasketItem("productName2", 10)
    BigDecimal productPrice2 = 3.4

    void "should calculate basket value with matching discounts"() {
        given:
        Discount basketItem1Discount = new Discount(basketItem1.productName, 0.1, null, null, MIN, MAX)
        Discount basketItem2Discount = new Discount(basketItem2.productName, 0.2, null, null, MIN, MAX)

        and:
        BigDecimal expectedProduct1Price = (productPrice1 * basketItem1.quantity) * basketItem1Discount.multiplier
        BigDecimal expectedProduct2Price = (productPrice2 * basketItem2.quantity) * basketItem2Discount.multiplier

        and:
        List<BasketItem> basketItems = [basketItem1, basketItem2]
        LocalDateTime dateTime = LocalDateTime.now()

        when:
        BigDecimal basketValue = basketCalculationService.calculateBasketValue(basketItems, dateTime)

        then:
        basketValue == expectedProduct1Price + expectedProduct2Price

        and:
        1 * productRepository.findProductPrice(basketItem1.productName) >> productPrice1
        1 * productRepository.findProductPrice(basketItem2.productName) >> productPrice2
        1 * discountRepository.findValidDiscounts([basketItem1.productName, basketItem2.productName].toSet(), dateTime) >> [basketItem1Discount, basketItem2Discount]
    }

    void "should calculate basket value with matching discount and required basket item"() {
        given:
        RequiredBasketItem requiredBasketItem = new RequiredBasketItem(basketItem1.productName, 1)
        Discount basketItem2Discount = new Discount(basketItem2.productName, 0.1, requiredBasketItem, null, MIN, MAX)

        and:
        BigDecimal expectedProduct1Price = (productPrice1 * basketItem1.quantity)
        BigDecimal expectedProduct2Price = (productPrice2 * basketItem2.quantity) * basketItem2Discount.multiplier

        when:
        BigDecimal basketValue = basketCalculationService.calculateBasketValue([basketItem1, basketItem2], LocalDateTime.now())

        then:
        basketValue == expectedProduct1Price + expectedProduct2Price

        and:
        1 * productRepository.findProductPrice(basketItem1.productName) >> productPrice1
        1 * productRepository.findProductPrice(basketItem2.productName) >> productPrice2
        1 * discountRepository.findValidDiscounts(_, _) >> [basketItem2Discount]
    }

    void "should calculate basket value with matching discount and required basket item not met"() {
        given:
        RequiredBasketItem requiredBasketItem = new RequiredBasketItem(basketItem1.productName, basketItem1.quantity + 1)
        Discount basketItem2Discount = new Discount(basketItem2.productName, 0.1, requiredBasketItem, null, MIN, MAX)

        and:
        BigDecimal expectedProduct1Price = (productPrice1 * basketItem1.quantity)
        BigDecimal expectedProduct2Price = (productPrice2 * basketItem2.quantity)

        when:
        BigDecimal basketValue = basketCalculationService.calculateBasketValue([basketItem1, basketItem2], LocalDateTime.now())

        then:
        basketValue == expectedProduct1Price + expectedProduct2Price

        and:
        1 * productRepository.findProductPrice(basketItem1.productName) >> productPrice1
        1 * productRepository.findProductPrice(basketItem2.productName) >> productPrice2
        1 * discountRepository.findValidDiscounts(_, _) >> [basketItem2Discount]
    }

    void "should calculate basket value with matching discount and partial quantity discounted"() {
        given:
        Discount basketItem1Discount = new Discount(basketItem1.productName, 0.1, null, 2, MIN, MAX)

        and:
        BigDecimal quantityWithoutDiscount = basketItem1.quantity - basketItem1Discount.discountQuantity
        BigDecimal quantityWithDiscount = basketItem1Discount.discountQuantity
        BigDecimal expectedProduct1Price = (productPrice1 * quantityWithoutDiscount) + (productPrice1 * quantityWithDiscount * basketItem1Discount.multiplier)

        when:
        BigDecimal basketValue = basketCalculationService.calculateBasketValue([basketItem1], LocalDateTime.now())

        then:
        basketValue == expectedProduct1Price

        and:
        1 * productRepository.findProductPrice(basketItem1.productName) >> productPrice1
        1 * discountRepository.findValidDiscounts(_, _) >> [basketItem1Discount]
    }

    void "should calculate basket value with matching discount and full quantity discounted"() {
        given:
        Discount basketItem1Discount = new Discount(basketItem1.productName, 0.1, null, basketItem1.quantity, MIN, MAX)

        and:
        BigDecimal expectedProduct1Price = (productPrice1 * basketItem1.quantity * basketItem1Discount.multiplier)

        when:
        BigDecimal basketValue = basketCalculationService.calculateBasketValue([basketItem1], LocalDateTime.now())

        then:
        basketValue == expectedProduct1Price

        and:
        1 * productRepository.findProductPrice(basketItem1.productName) >> productPrice1
        1 * discountRepository.findValidDiscounts(_, _) >> [basketItem1Discount]
    }

    void "should calculate basket value with no matching discounts"() {
        given:
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
