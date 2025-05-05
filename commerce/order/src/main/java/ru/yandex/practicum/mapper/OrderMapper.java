package ru.yandex.practicum.mapper;

import ru.yandex.practicum.dto.order.dto.OrderDto;
import ru.yandex.practicum.entity.Order;
import ru.yandex.practicum.entity.OrderProducts;

import java.util.stream.Collectors;

public class OrderMapper {

    //Оставил ручной маппер для корректной передачи продуктов из сущности в мап
    public static OrderDto toDto(Order order) {
        return OrderDto.builder()
                .orderId(order.getOrderId())
                .shoppingCartId(order.getShoppingCartId())
                .products(order.getProducts().stream()
                        .collect(Collectors.toMap(OrderProducts::getProductId, OrderProducts::getQuantity)))
                .paymentId(order.getPaymentId())
                .deliveryId(order.getDeliveryId())
                .state(order.getState())
                .deliveryWeight(order.getDeliveryWeight())
                .deliveryVolume(order.getDeliveryVolume())
                .fragile(order.getFragile())
                .totalPrice(order.getTotalPrice())
                .deliveryPrice(order.getDeliveryPrice())
                .productPrice(order.getProductPrice())
                .build();
    }
}
