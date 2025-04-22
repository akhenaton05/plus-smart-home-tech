package ru.yandex.practicum.dto.warehouse.exception;

import lombok.Getter;

@Getter
public class ProductInShoppingCartLowQuantityInWarehouse extends RuntimeException {
    private final String userMessage;

    public ProductInShoppingCartLowQuantityInWarehouse(String message, String userMessage) {
        super(message);
        this.userMessage = userMessage;
    }
}
