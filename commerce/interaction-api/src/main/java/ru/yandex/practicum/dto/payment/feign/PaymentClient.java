package ru.yandex.practicum.dto.payment.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.order.dto.OrderDto;
import ru.yandex.practicum.dto.payment.dto.PaymentDto;

import java.util.UUID;

@FeignClient(name = "payment",  path = "/api/v1/payment", configuration = PaymentFeignConfig.class, fallbackFactory = PaymentClientFallbackFactory.class)
public interface PaymentClient {

    @PostMapping
    PaymentDto createPayment(@RequestBody OrderDto dto);

    @PostMapping("/totalCost")
    double calculateTotalCost(@RequestBody OrderDto dto);

    @PostMapping("/productCost")
    double calculateProductCost(@RequestBody OrderDto dto);

    @PostMapping("/refund")
    void refund(@RequestBody UUID paymentId);

    @PostMapping("/failed")
    void failed(@RequestBody UUID paymentId);
}
