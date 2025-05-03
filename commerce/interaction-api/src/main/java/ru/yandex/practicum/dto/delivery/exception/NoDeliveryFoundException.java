package ru.yandex.practicum.dto.delivery.exception;

public class NoDeliveryFoundException extends RuntimeException {
    private final String userMessage;

    public NoDeliveryFoundException(String message, String userMessage) {
        super(message);
        this.userMessage = userMessage;
    }
}
