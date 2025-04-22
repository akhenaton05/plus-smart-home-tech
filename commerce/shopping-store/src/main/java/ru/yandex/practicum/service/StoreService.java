package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.store.ProductDto;
import ru.yandex.practicum.dto.store.entity.ProductCategory;
import ru.yandex.practicum.dto.store.entity.QuantityState;

import java.util.List;
import java.util.UUID;


public interface StoreService {
    List<ProductDto> getProducts(ProductCategory productCategory, int page, int size, String sort);

    ProductDto getProductsById(UUID id);

    ProductDto saveProduct(ProductDto dto);

    ProductDto updateProduct(ProductDto dto);

    Boolean removeProduct(UUID id);

    Boolean setQuantityState(UUID productId, QuantityState quantityState);
}
