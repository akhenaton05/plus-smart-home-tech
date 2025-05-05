package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.order.dto.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.dto.OrderDto;
import ru.yandex.practicum.dto.order.dto.ProductReturnRequest;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderDto createOrder(CreateNewOrderRequest request);

    List<OrderDto> getUsersOrders(String username);

    OrderDto returnOrder(ProductReturnRequest request);

    OrderDto orderPayment(UUID orderId);

    OrderDto orderFailedPayment(UUID orderId);

    OrderDto orderDelivery(UUID orderId);

    OrderDto orderFailedDelivery(UUID orderId);

    OrderDto orderCompleted(UUID orderId);

    OrderDto orderCalculate(UUID orderId);

    OrderDto orderCalculateDelivery(UUID orderId);

    OrderDto orderAssembly(UUID orderId);

    OrderDto orderFailedAssembly(UUID orderId);
}
