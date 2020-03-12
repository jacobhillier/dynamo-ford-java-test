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

    DiscountRepository discountRepository = new DiscountRepository()

    @Unroll
    void "should find matching discounts"() {
        when:
        List<Discount> actualDiscounts = discountRepository.findValidDiscounts([BREAD_DISCOUNT.productName].toSet(), dateTime)

        then:
        actualDiscounts == expectedDiscounts

        where:
        dateTime                                 | expectedDiscounts
        BREAD_DISCOUNT.validFrom                 | [BREAD_DISCOUNT]
        BREAD_DISCOUNT.validTo                   | [BREAD_DISCOUNT]
        BREAD_DISCOUNT.validFrom.minusSeconds(1) | []
        BREAD_DISCOUNT.validTo.plusSeconds(1)    | []
    }
}
