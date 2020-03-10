package com.github.jacobhillier.dynamoford

import com.github.jacobhillier.dynamoford.model.BasketItem
import spock.lang.Specification

import java.time.LocalDateTime

class ArgumentParserTest extends Specification {

    ArgumentParser argumentParser = new ArgumentParser()

    void "should parse valid arguments"() {
        given:
        String productName1 = "foo"
        int quantity1 = 1
        BasketItem expectedBasketItem1 = new BasketItem(productName1, quantity1)

        and:
        String productName2 = "bar"
        int quantity2 = 2
        BasketItem expectedBasketItem2 = new BasketItem(productName2, quantity2)

        and:
        LocalDateTime dateTime = LocalDateTime.now()

        when:
        ParsedArguments parsedArguments = argumentParser.parse(
                "-i", "${productName1}=${quantity1}",
                "-i", "${productName2}=${quantity2}",
                "-d", dateTime as String
        )

        then:
        parsedArguments == new ParsedArguments([expectedBasketItem1, expectedBasketItem2], dateTime)
    }
}