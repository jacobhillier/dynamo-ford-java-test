package com.github.jacobhillier.dynamoford;

import com.github.jacobhillier.dynamoford.service.BasketCalculationService;
import org.apache.commons.cli.ParseException;

import java.math.BigDecimal;

public class Main {

    private final ArgumentParser argumentParser;
    private final BasketCalculationService basketCalculationService;

    public Main(ArgumentParser argumentParser, BasketCalculationService basketCalculationService) {
        this.argumentParser = argumentParser;
        this.basketCalculationService = basketCalculationService;
    }

    public BigDecimal parseArgsAndCalculate(String[] args) throws ParseException {
        ParsedArguments parsedArguments = argumentParser.parse(args);

        return basketCalculationService.calculateBasketValue(parsedArguments.getBasketItems(), parsedArguments.getDateTime());
    }

    public static void main(String[] args) throws ParseException {
        Main main = new Main(new ArgumentParser(), new BasketCalculationService());
        main.parseArgsAndCalculate(args);
    }
}
