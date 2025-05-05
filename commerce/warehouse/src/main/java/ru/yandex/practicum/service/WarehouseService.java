package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.warehouse.*;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;

import java.util.Map;
import java.util.UUID;


public interface WarehouseService {
    BookedProductsDto checkProducts(ShoppingCartDto dto);

    void saveToWarehouse(NewProductInWarehouseRequest request);

    void addProductQuantity(AddProductToWarehouseRequest request);

    AddressDto getWarehouseAddress();

    void shippedToDelivery(ShippedToDeliveryRequest request);

    BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request);

    void acceptReturn(Map<UUID, Long> returnProducts);
}
