package com.github.jacobhillier.dynamoford.data

import com.github.jacobhillier.dynamoford.model.Discount
import com.github.jacobhillier.dynamoford.model.RequiredBasketItem
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDateTime

class DiscountRepositoryTest extends Specification {

    static final Discount BREAD_DISCOUNT = new Discount(
            "bread",
            0.5,
            new RequiredBasketItem("soup", 2),
            1,
            LocalDateTime.of(2020, 3, 11, 0, 0, 0),
            LocalDateTime.of(2020, 3, 17, 23, 59, 59)
    )
    static final Discount APPLE_DISCOUNT = new Discount(
            "apple",
            BigDecimal.valueOf(0.9),
            null,
            null,
            LocalDateTime.of(2020, 3, 15, 0, 0, 0),
            LocalDateTime.of(2020, 4, 30, 23, 59, 59)
    )

    DiscountRepository discountRepository = new DiscountRepository()

    @Unroll
    void "should find matching discounts"() {
        when:
        List<Discount> actualDiscounts = discountRepository.findValidDiscounts([productName].toSet(), dateTime)

        then:
        actualDiscounts == expectedDiscounts

        where:
        productName                | dateTime                                 | expectedDiscounts
        BREAD_DISCOUNT.productName | BREAD_DISCOUNT.validFrom                 | [BREAD_DISCOUNT]
        BREAD_DISCOUNT.productName | BREAD_DISCOUNT.validTo                   | [BREAD_DISCOUNT]
        BREAD_DISCOUNT.productName | BREAD_DISCOUNT.validFrom.minusSeconds(1) | []
        BREAD_DISCOUNT.productName | BREAD_DISCOUNT.validTo.plusSeconds(1)    | []
        APPLE_DISCOUNT.productName | APPLE_DISCOUNT.validFrom                 | [APPLE_DISCOUNT]
        APPLE_DISCOUNT.productName | APPLE_DISCOUNT.validTo                   | [APPLE_DISCOUNT]
        APPLE_DISCOUNT.productName | APPLE_DISCOUNT.validFrom.minusSeconds(1) | []
        APPLE_DISCOUNT.productName | APPLE_DISCOUNT.validTo.plusSeconds(1)    | []
        "foo"                      | LocalDateTime.now()                      | []
    }
}
