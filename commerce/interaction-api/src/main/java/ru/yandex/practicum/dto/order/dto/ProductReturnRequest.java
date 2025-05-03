package ru.yandex.practicum.dto.order.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class ProductReturnRequest {
    private UUID orderId;
    @NonNull
    private Map<UUID, Long> products;
}
