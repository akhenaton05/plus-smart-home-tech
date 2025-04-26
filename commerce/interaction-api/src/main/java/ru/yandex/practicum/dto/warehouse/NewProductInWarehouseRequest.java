package ru.yandex.practicum.dto.warehouse;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class NewProductInWarehouseRequest {
    private UUID productId;
    private boolean fragile;
    private DimensionDto dimension;
    private double wight;
}
