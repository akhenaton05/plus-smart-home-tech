package ru.yandex.practicum.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class InsufficientProductInfo {
    private UUID productId;
    private long missingQuantity;
}
