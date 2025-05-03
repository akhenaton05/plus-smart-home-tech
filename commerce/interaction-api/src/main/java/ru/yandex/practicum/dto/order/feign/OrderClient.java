package ru.yandex.practicum.dto.order.feign;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.order.dto.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.dto.OrderDto;
import ru.yandex.practicum.dto.order.dto.ProductReturnRequest;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "order", path = "/api/v1/order", configuration = OrderFeignConfig.class, fallbackFactory = OrderClientFallbackFactory.class)
public interface OrderClient {

    @PutMapping
    OrderDto createOrder(@RequestBody CreateNewOrderRequest request);

    @GetMapping
    List<OrderDto> getUsersOrders(@RequestParam String username);

    @PostMapping("/return")
    OrderDto returnOrder(@Valid @RequestBody ProductReturnRequest request);

    @PostMapping("/payment")
    OrderDto orderPayment(@RequestBody UUID orderId);

    @PostMapping("/payment/failed")
    OrderDto orderFailedPayment(@RequestBody UUID orderId);

    @PostMapping("/delivery")
    OrderDto orderDelivery(@RequestBody UUID orderId);

    @PostMapping("/delivery/failed")
    OrderDto orderFailedDelivery(@RequestBody UUID orderId);

    @PostMapping("/completed")
    OrderDto orderCompleted(@RequestBody UUID orderId);

    @PostMapping("/calculate/total")
    OrderDto orderCalculate(@RequestBody UUID orderId);

    @PostMapping("/calculate/delivery")
    OrderDto orderCalculateDelivery(@RequestBody UUID orderId);

    @PostMapping("/assembly")
    OrderDto orderAssembly(@RequestBody UUID orderId);

    @PostMapping("/assembly/failed")
    OrderDto orderFailedAssembly(@RequestBody UUID orderId);
}
