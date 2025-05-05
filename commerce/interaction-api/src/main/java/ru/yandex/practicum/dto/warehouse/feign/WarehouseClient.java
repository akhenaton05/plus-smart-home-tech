package ru.yandex.practicum.dto.warehouse.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.config.FeignConfig;
import ru.yandex.practicum.dto.warehouse.*;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse", configuration = FeignConfig.class, fallbackFactory = WarehouseClientFallbackFactory.class)
public interface WarehouseClient {

    @PostMapping("/check")
    @ResponseStatus(HttpStatus.OK)
    BookedProductsDto checkProducts(@RequestBody ShoppingCartDto dto);

    @GetMapping("/address")
    @ResponseStatus(HttpStatus.OK)
    AddressDto getWarehouseAddress();

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    void saveToWarehouse(@RequestBody NewProductInWarehouseRequest request);

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.OK)
    void addProducts(@RequestBody AddProductToWarehouseRequest request);

    @PostMapping("/assembly")
    @ResponseStatus(HttpStatus.OK)
    BookedProductsDto assemblyProductsForOrder(@RequestBody AssemblyProductsForOrderRequest request);

    @PostMapping("/shipped")
    @ResponseStatus(HttpStatus.OK)
    void shippedToDelivery(@RequestBody ShippedToDeliveryRequest request);

    @PostMapping("/return")
    @ResponseStatus(HttpStatus.OK)
    void returnProducts(@RequestBody Map<UUID, Long> returnProducts);
}