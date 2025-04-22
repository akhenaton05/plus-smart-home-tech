package ru.yandex.practicum.dto.cart;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ChangeProductQuantityRequest {
    private UUID productId;
    private long newQuantity;
}
