package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;


public interface WarehouseService {
    BookedProductsDto checkProducts(ShoppingCartDto dto);

    void saveToWarehouse(NewProductInWarehouseRequest request);

    void addProductQuantity(AddProductToWarehouseRequest request);

    AddressDto getWarehouseAddress();
}
