package ru.yandex.practicum.dto.delivery.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.delivery.dto.DeliveryDto;
import ru.yandex.practicum.dto.order.dto.OrderDto;

import java.util.UUID;

@FeignClient(name = "delivery", path = "/api/v1/delivery", configuration = DeliveryFeignConfig.class, fallbackFactory = DeliveryClientFallbackFactory.class)
public interface DeliveryClient {

    @PutMapping
    DeliveryDto planDelivery(@RequestBody DeliveryDto dto);

    @PostMapping("/cost")
    double calculateDeliveryCost(@RequestBody OrderDto dto);

    @PostMapping("/successful")
    void successfulDelivery(@RequestBody UUID orderId);

    @PostMapping("/failed")
    void failedDelivery(@RequestBody UUID orderId);

    @PostMapping("/picked")
    void pickedDelivery(@RequestBody UUID orderId);
}
