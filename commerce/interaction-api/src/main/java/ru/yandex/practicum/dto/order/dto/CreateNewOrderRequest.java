package ru.yandex.practicum.dto.order.dto;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.AddressDto;

@Data
@Builder
public class CreateNewOrderRequest {
    private ShoppingCartDto shoppingCart;
    private AddressDto deliveryAddress;
}
