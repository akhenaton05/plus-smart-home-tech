package ru.yandex.practicum.dto.order.exception;

public class NoOrderFoundException extends RuntimeException {
    private final String userMessage;

    public NoOrderFoundException(String message, String userMessage) {
        super(message);
        this.userMessage = userMessage;
    }
}
