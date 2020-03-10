package com.github.jacobhillier.dynamoford;

import com.github.jacobhillier.dynamoford.model.BasketItem;
import org.apache.commons.cli.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ArgumentParser {

    private static final Options CLI_OPTIONS = new Options()
            .addOption(Option.builder("i").longOpt("item").hasArg(true).required(true).build())
            .addOption(Option.builder("d").longOpt("date").hasArg(true).required(true).build());
    private static final String PRODUCT_NAME_QUANTITY_DELIMITER = "=";

    private final CommandLineParser commandLineParser;

    public ArgumentParser() {
        this.commandLineParser = new DefaultParser();
    }

    public ParsedArguments parse(String[] args) throws ParseException {
        CommandLine parsedArgs = commandLineParser.parse(CLI_OPTIONS, args);

        List<BasketItem> basketItems = extractBasketItemsFromParsedArgs(parsedArgs);
        LocalDateTime dateTime = extractDateTimeFromParsedArgs(parsedArgs);

        return new ParsedArguments(basketItems, dateTime);
    }

    private List<BasketItem> extractBasketItemsFromParsedArgs(CommandLine parsedArgs) {
        return Stream.of(parsedArgs.getOptionValues("i"))
                .map(this::convertToBasketItem)
                .collect(Collectors.toList());
    }

    private BasketItem convertToBasketItem(String item) {
        String[] split = item.split(PRODUCT_NAME_QUANTITY_DELIMITER);
        String productName = split[0];
        int quantity = Integer.valueOf(split[1]);
        return new BasketItem(productName, quantity);
    }

    private LocalDateTime extractDateTimeFromParsedArgs(CommandLine parsedArgs) {
        String dateTimeString = parsedArgs.getOptionValue("d");
        return LocalDateTime.parse(dateTimeString);
    }
}
