package ru.yandex.practicum.dto.payment.exception;

public class NotEnoughInfoInOrderToCalculateException extends RuntimeException {
    private final String userMessage;

    public NotEnoughInfoInOrderToCalculateException(String message, String userMessage) {
        super(message);
        this.userMessage = userMessage;
    }
}
