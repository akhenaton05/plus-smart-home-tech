package ru.yandex.practicum.dto.cart.exception;

import lombok.Getter;
import ru.yandex.practicum.dto.cart.InsufficientProductInfo;

import java.util.*;

@Getter
public class NoProductsInShoppingCartException extends RuntimeException {
    private final String userMessage;
    private final List<InsufficientProductInfo> details;

    public NoProductsInShoppingCartException(String message, String userMessage) {
        super(message);
        this.userMessage = userMessage;
        this.details = null;
    }

    public NoProductsInShoppingCartException(String message, String userMessage,
                                             List<InsufficientProductInfo> details) {
        super(message);
        this.userMessage = userMessage;
        this.details = details;
    }
}
