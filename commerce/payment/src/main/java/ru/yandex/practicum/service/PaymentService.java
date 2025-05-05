package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.order.dto.OrderDto;
import ru.yandex.practicum.dto.payment.dto.PaymentDto;

import java.util.UUID;

public interface PaymentService {
    PaymentDto createPayment(OrderDto dto);

    double calculateProductCost(OrderDto dto);

    double calculateTotalCost(OrderDto dto);

    void successPayment(UUID paymentId);

    void failedPayment(UUID paymentId);
}
