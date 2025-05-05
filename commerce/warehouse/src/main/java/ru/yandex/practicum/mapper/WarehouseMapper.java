package ru.yandex.practicum.mapper;

import org.mapstruct.*;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.entity.Product;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {

    @Mapping(source = "wight", target = "weight")
    @Mapping(source = "fragile", target = "isFragile")
    @Mapping(source = "dimension.width", target = "width")
    @Mapping(source = "dimension.height", target = "height")
    @Mapping(source = "dimension.depth", target = "depth")
    @Mapping(constant = "0L", target = "quantity")
    Product requestToProduct(NewProductInWarehouseRequest request);
}