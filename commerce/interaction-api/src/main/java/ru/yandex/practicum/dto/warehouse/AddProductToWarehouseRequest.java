package ru.yandex.practicum.dto.warehouse;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AddProductToWarehouseRequest {
    private UUID productId;
    private long quantity;
}
