package ru.yandex.practicum.mapper;

import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.entity.Product;

public class WarehouseMapper {

    public static Product requestToProduct(NewProductInWarehouseRequest request) {
        return Product.builder()
                .productId(request.getProductId())
                .width(request.getDimension().getWidth())
                .height(request.getDimension().getHeight())
                .depth(request.getDimension().getDepth())
                .weight(request.getWight())
                .isFragile(request.isFragile())
                .quantity(0L)
                .build();
    }
}
