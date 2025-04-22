package ru.yandex.practicum.dto.warehouse.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.config.FeignConfig;
import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;

@FeignClient(name = "warehouse", configuration = FeignConfig.class, fallbackFactory = WarehouseClientFallbackFactory.class)
public interface WarehouseClient {

    @PostMapping("/api/v1/warehouse/check")
    @ResponseStatus(HttpStatus.OK)
    BookedProductsDto checkProducts(@RequestBody ShoppingCartDto dto);

    @GetMapping("/api/v1/warehouse/address")
    @ResponseStatus(HttpStatus.OK)
    AddressDto getWarehouseAddress();

    @PutMapping("/api/v1/warehouse")
    @ResponseStatus(HttpStatus.OK)
    void saveToWarehouse(@RequestBody NewProductInWarehouseRequest request);

    @PostMapping("/api/v1/warehouse/add")
    @ResponseStatus(HttpStatus.OK)
    void addProducts(@RequestBody AddProductToWarehouseRequest request);
}