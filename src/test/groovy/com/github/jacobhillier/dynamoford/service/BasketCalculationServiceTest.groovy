package com.github.jacobhillier.dynamoford.service

import com.github.jacobhillier.dynamoford.data.ProductRepository
import com.github.jacobhillier.dynamoford.model.BasketItem
import spock.lang.Specification

class BasketCalculationServiceTest extends Specification {

    ProductRepository productRepository = Mock(ProductRepository)
    BasketCalculationService basketCalculationService = new BasketCalculationService(productRepository)

    void "should calculate basket value without discounts"() {
        given:
        BasketItem basketItem1 = new BasketItem("productName1", 1)
        BigDecimal productPrice1 = new BigDecimal("1.2")

        and:
        BasketItem basketItem2 = new BasketItem("productName2", 2)
        BigDecimal productPrice2 = new BigDecimal("3.4")

        when:
        BigDecimal basketValue = basketCalculationService.calculateBasketValue([basketItem1, basketItem2], null)

        then:
        basketValue == (productPrice1 * basketItem1.quantity) + (productPrice2 * basketItem2.quantity)

        and:
        1 * productRepository.findProductPrice(basketItem1.productName) >> productPrice1
        1 * productRepository.findProductPrice(basketItem2.productName) >> productPrice2
    }
}
