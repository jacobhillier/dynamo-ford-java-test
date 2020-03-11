package com.github.jacobhillier.dynamoford.data

import spock.lang.Specification
import spock.lang.Unroll

class ProductRepositoryTest extends Specification {

    ProductRepository productRepository = new ProductRepository()

    @Unroll
    void "should return price for known products"() {
        expect:
        productRepository.findProductPrice(productName) == expectedPrice

        where:
        productName | expectedPrice
        "soup"      | 0.65
        "bread"     | 0.80
        "milk"      | 1.30
        "apples"    | 0.10
    }
}
