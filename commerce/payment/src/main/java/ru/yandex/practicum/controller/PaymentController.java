package ru.yandex.practicum.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.order.dto.OrderDto;
import ru.yandex.practicum.dto.payment.dto.PaymentDto;
import ru.yandex.practicum.service.PaymentService;

import java.util.UUID;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/payment")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public PaymentDto createPayment(@RequestBody OrderDto dto) {
        log.info("Creating payment for {}", dto);
        return paymentService.createPayment(dto);
    }

    @PostMapping("/totalCost")
    public double calculateTotalCost(@RequestBody OrderDto dto) {
        log.info("Creating request for total cost calculating {}", dto);
        return paymentService.calculateTotalCost(dto);
    }

    @PostMapping("/productCost")
    public double calculateProductCost(@RequestBody OrderDto dto) {
        log.info("Creating request for product cost calculating {}", dto);
        return paymentService.calculateProductCost(dto);
    }

    @PostMapping("/refund")
    public void refund(@RequestBody UUID paymentId) {
        log.info("Emulating refund for {}", paymentId);
        paymentService.successPayment(paymentId);
    }

    @PostMapping("/failed")
    public void failed(@RequestBody UUID paymentId) {
        log.info("Emulating failed payment for {}", paymentId);
        paymentService.failedPayment(paymentId);
    }
}
