package com.github.jacobhillier.dynamoford;

import com.github.jacobhillier.dynamoford.model.BasketItem;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ParsedArguments {

    private final List<BasketItem> basketItems;
    private final LocalDateTime dateTime;
}
