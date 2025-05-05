package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.order.dto.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.dto.OrderDto;
import ru.yandex.practicum.dto.order.dto.ProductReturnRequest;
import ru.yandex.practicum.service.OrderService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/order")
public class OrderController {
    private final OrderService orderService;

    @PutMapping
    public OrderDto createOrder(@RequestBody CreateNewOrderRequest request) {
        log.info("New request {} for creating order", request);
        return orderService.createOrder(request);
    }

    @GetMapping
    public List<OrderDto> getUsersOrders(@RequestParam String username) {
        log.info("New request for getting {} orders", username);
        return orderService.getUsersOrders(username);
    }

    @PostMapping("/return")
    public OrderDto returnOrder(@Valid @RequestBody ProductReturnRequest request) {
        log.info("New request for returning {} order", request);
        return orderService.returnOrder(request);
    }

    @PostMapping("/payment")
    public OrderDto orderPayment(@RequestBody UUID orderId) {
        log.info("New request for order payment {}", orderId);
        return orderService.orderPayment(orderId);
    }

    @PostMapping("/payment/failed")
    public OrderDto orderFailedPayment(@RequestBody UUID orderId) {
        log.info("New request for failed order payment {}", orderId);
        return orderService.orderFailedPayment(orderId);
    }

    @PostMapping("/delivery")
    public OrderDto orderDelivery(@RequestBody UUID orderId) {
        log.info("New request for order delivery {}", orderId);
        return orderService.orderDelivery(orderId);
    }

    @PostMapping("/delivery/failed")
    public OrderDto orderFailedDelivery(@RequestBody UUID orderId) {
        log.info("New request for failed order delivery {}", orderId);
        return orderService.orderFailedDelivery(orderId);
    }

    @PostMapping("/completed")
    public OrderDto orderCompleted(@RequestBody UUID orderId) {
        log.info("New request for completed order {}", orderId);
        return orderService.orderCompleted(orderId);
    }

    @PostMapping("/calculate/total")
    public OrderDto orderCalculate(@RequestBody UUID orderId) {
        log.info("New request for total order calculate{}", orderId);
        return orderService.orderCalculate(orderId);
    }

    @PostMapping("/calculate/delivery")
    public OrderDto orderCalculateDelivery(@RequestBody UUID orderId) {
        log.info("New request for order calculate delivery {}", orderId);
        return orderService.orderCalculateDelivery(orderId);
    }

    @PostMapping("/assembly")
    public OrderDto orderAssembly(@RequestBody UUID orderId) {
        log.info("New request for order assembly {}", orderId);
        return orderService.orderAssembly(orderId);
    }

    @PostMapping("/assembly/failed")
    public OrderDto orderFailedAssembly(@RequestBody UUID orderId) {
        log.info("New request for failed order assembly {}", orderId);
        return orderService.orderFailedAssembly(orderId);
    }
}
