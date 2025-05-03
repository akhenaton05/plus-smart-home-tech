package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.delivery.dto.DeliveryDto;
import ru.yandex.practicum.dto.order.dto.OrderDto;

import java.util.UUID;

public interface DeliveryService {
    DeliveryDto planDelivery(DeliveryDto dto);

    double calculateDeliveryCost(OrderDto dto);

    void successfulDelivery(UUID orderId);

    void pickedDelivery(UUID orderId);

    void failedDelivery(UUID orderId);

}
