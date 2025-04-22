package ru.yandex.practicum.dto.store.exception;

import lombok.Getter;

@Getter
public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String message) {
        super(message);
    }
}
