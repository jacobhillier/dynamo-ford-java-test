package com.github.jacobhillier.dynamoford;

import com.github.jacobhillier.dynamoford.service.BasketCalculationService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.cli.ParseException;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class Main {

    private final ArgumentParser argumentParser;
    private final BasketCalculationService basketCalculationService;

    public BigDecimal parseArgsAndCalculate(String[] args) throws ParseException {
        ParsedArguments parsedArguments = argumentParser.parse(args);

        return basketCalculationService.calculateBasketValue(parsedArguments.getBasketItems(), parsedArguments.getDateTime());
    }

    public static void main(String[] args) throws ParseException {
        Main main = new Main(new ArgumentParser(), new BasketCalculationService());
        main.parseArgsAndCalculate(args);
    }
}
