package com.github.jacobhillier.dynamoford.main

import com.github.jacobhillier.dynamoford.model.BasketItem
import com.github.jacobhillier.dynamoford.model.ParsedArguments
import com.github.jacobhillier.dynamoford.service.BasketCalculationService
import spock.lang.Specification

import java.time.LocalDateTime

class MainTest extends Specification {

    ArgumentParser argumentParser = Mock(ArgumentParser)
    BasketCalculationService basketCalculationService = Mock(BasketCalculationService)
    Main main = new Main(argumentParser, basketCalculationService)

    void "should call argument parser then basket calculator"() {
        given:
        String[] args = ["foo", "bar"] as String[]
        ParsedArguments parsedArguments = new ParsedArguments([new BasketItem("foo", 1)], LocalDateTime.now())
        BigDecimal expectedBasketValue = new BigDecimal("1.23")

        when:
        BigDecimal actualBasketValue = main.parseArgsAndCalculate(args)

        then:
        actualBasketValue.is(expectedBasketValue)

        and:
        1 * argumentParser.parse(args) >> parsedArguments
        1 * basketCalculationService.calculateBasketValue(parsedArguments.basketItems, parsedArguments.dateTime) >> expectedBasketValue
    }
}