package ru.yandex.practicum.dto.warehouse.exception;

import lombok.Getter;

@Getter
public class SpecifiedProductAlreadyInWarehouseException extends RuntimeException {
    private final String userMessage;

    public SpecifiedProductAlreadyInWarehouseException(String message, String userMessage) {
        super(message);
        this.userMessage = userMessage;
    }
}
