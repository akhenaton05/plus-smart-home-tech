package ru.yandex.practicum.dto.cart.exception;

import lombok.Getter;

@Getter
public class NotAuthorizedUserException extends RuntimeException {
    private final String userMessage;

    public NotAuthorizedUserException(String message, String userMessage) {
        super(message);
        this.userMessage = userMessage;
    }
}
