package com.github.jacobhillier.dynamoford.main;

import com.github.jacobhillier.dynamoford.data.DiscountRepository;
import com.github.jacobhillier.dynamoford.data.ProductRepository;
import com.github.jacobhillier.dynamoford.model.ParsedArguments;
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
        ProductRepository productRepository = new ProductRepository();
        DiscountRepository discountRepository = new DiscountRepository();

        ArgumentParser argumentParser = new ArgumentParser();
        BasketCalculationService basketCalculationService = new BasketCalculationService(productRepository, discountRepository);

        Main main = new Main(argumentParser, basketCalculationService);
        System.out.println(main.parseArgsAndCalculate(args));
    }
}
