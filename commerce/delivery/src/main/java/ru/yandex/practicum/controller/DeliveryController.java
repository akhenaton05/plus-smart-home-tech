package ru.yandex.practicum.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.delivery.dto.DeliveryDto;
import ru.yandex.practicum.dto.order.dto.OrderDto;
import ru.yandex.practicum.service.DeliveryService;

import java.util.UUID;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/delivery")
public class DeliveryController {
    private final DeliveryService deliveryService;

    @PutMapping
    public DeliveryDto planDelivery(@RequestBody DeliveryDto dto) {
        log.info("New request for creating delivery {}", dto);
        return deliveryService.planDelivery(dto);
    }

    @PostMapping("/cost")
    public double calculateDeliveryCost(@RequestBody OrderDto dto) {
        log.info("New request for order delivery calculating {}", dto);
        return deliveryService.calculateDeliveryCost(dto);
    }

    @PostMapping("/successful")
    public void successfulDelivery(@RequestBody UUID orderId) {
        log.info("New request for emulating successful order delivery {}", orderId);
        deliveryService.successfulDelivery(orderId);
    }

    @PostMapping("/failed")
    public void failedDelivery(@RequestBody UUID orderId) {
        log.info("New request for emulating failed delivery {}", orderId);
        deliveryService.failedDelivery(orderId);
    }

    @PostMapping("/picked")
    public void pickedDelivery(@RequestBody UUID orderId) {
        log.info("New request for emulating picking delivery {}", orderId);
        deliveryService.pickedDelivery(orderId);
    }
}
