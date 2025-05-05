package ru.yandex.practicum.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.warehouse.*;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.service.WarehouseService;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/warehouse")
public class WarehouseController {
    private final WarehouseService warehouseService;

    @GetMapping("/address")
    @ResponseStatus(HttpStatus.OK)
    public AddressDto getWarehouseAddress() {
        log.info("New 'GET' request for warehouse address");
        return warehouseService.getWarehouseAddress();
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public void saveToWarehouse(@RequestBody NewProductInWarehouseRequest request) {
        log.info("New 'PUT' create request for {}", request);
        warehouseService.saveToWarehouse(request);
    }

    @PostMapping("/check")
    @ResponseStatus(HttpStatus.OK)
    public BookedProductsDto checkProducts(@RequestBody ShoppingCartDto dto) {
        log.info("New 'POST' request to check {} ", dto);
        return warehouseService.checkProducts(dto);
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.OK)
    public void addProducts(@RequestBody AddProductToWarehouseRequest request) {
        log.info("New 'POST' request to add {} ", request);
        warehouseService.addProductQuantity(request);
    }

    @PostMapping("/assembly")
    @ResponseStatus(HttpStatus.OK)
    public BookedProductsDto assemblyProductsForOrder(@RequestBody AssemblyProductsForOrderRequest request) {
        log.info("New 'POST' request for assembling order {} ", request);
        return warehouseService.assemblyProductsForOrder(request);
    }

    @PostMapping("/shipped")
    @ResponseStatus(HttpStatus.OK)
    public void shippedToDelivery(@RequestBody ShippedToDeliveryRequest request) {
        log.info("New 'POST' request for shipping order {} ", request);
        warehouseService.shippedToDelivery(request);
    }


    @PostMapping("/return")
    @ResponseStatus(HttpStatus.OK)
    public void returnProducts(@RequestBody Map<UUID, Long> returnProducts) {
        log.info("New 'POST' request for returning order {} ", returnProducts);
        warehouseService.acceptReturn(returnProducts);
    }
}
