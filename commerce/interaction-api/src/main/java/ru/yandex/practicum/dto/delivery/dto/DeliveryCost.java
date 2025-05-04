package ru.yandex.practicum.dto.delivery.dto;

public enum DeliveryCost {
    BASE_COST(5.0),
    WEIGHT_FACTOR(0.3),
    VOLUME_FACTOR(0.2),
    FRAGILE_FACTOR(0.2),
    ADDRESS_FACTOR(0.2);

    private final double value;

    DeliveryCost(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}
