package ru.yandex.practicum.dto.warehouse.exception;

import lombok.Getter;
import ru.yandex.practicum.dto.cart.InsufficientProductInfo;

import java.util.List;

@Getter
public class ProductInShoppingCartLowQuantityInWarehouse extends RuntimeException {
    private final String userMessage;
    private final List<InsufficientProductInfo> details;

    public ProductInShoppingCartLowQuantityInWarehouse(String message, String userMessage) {
        super(message);
        this.userMessage = userMessage;
        this.details = null;
    }

    public ProductInShoppingCartLowQuantityInWarehouse(String message, String userMessage,
                                             List<InsufficientProductInfo> details) {
        super(message);
        this.userMessage = userMessage;
        this.details = details;
    }
}
