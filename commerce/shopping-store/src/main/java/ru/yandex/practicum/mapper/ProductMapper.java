package ru.yandex.practicum.mapper;

import ru.yandex.practicum.dto.store.ProductDto;
import ru.yandex.practicum.entity.Product;

public class ProductMapper {

    public static Product dtoToProduct(ProductDto dto) {
        return Product.builder()
                .productId(dto.getProductId())
                .productName(dto.getProductName())
                .description(dto.getDescription())
                .imageSrc(dto.getImageSrc())
                .productState(dto.getProductState())
                .quantityState(dto.getQuantityState())
                .productCategory(dto.getProductCategory())
                .price(dto.getPrice())
                .build();
    }

    public static ProductDto toDto(Product product) {
        return ProductDto.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .description(product.getDescription())
                .imageSrc(product.getImageSrc())
                .productState(product.getProductState())
                .quantityState(product.getQuantityState())
                .productCategory(product.getProductCategory())
                .price(product.getPrice())
                .build();
    }
}
