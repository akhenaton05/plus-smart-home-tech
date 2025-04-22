package ru.yandex.practicum.dto.store;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.dto.store.entity.ProductCategory;
import ru.yandex.practicum.dto.store.entity.ProductState;
import ru.yandex.practicum.dto.store.entity.QuantityState;

import java.util.UUID;

@Data
@Builder
public class ProductDto {
    private UUID productId;
    @NotBlank(message = "Field: name. Error: must not be blank. Value: empty")
    private String productName;
    @NotBlank(message = "Field: description. Error: must not be blank. Value: empty")
    private String description;
    private String imageSrc;
    private QuantityState quantityState;
    private ProductState productState;
    private ProductCategory productCategory;
    @Min(1)
    private double price;
}
