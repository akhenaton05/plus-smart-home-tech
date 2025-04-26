package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;


public interface CartService {
    ShoppingCartDto saveCart(String username, Map<UUID, Long> cartProducts);

    ShoppingCartDto getCart(String username);

    void deleteCart(String username);

    ShoppingCartDto removeProductCart(String username, List<UUID> productIds);

    ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request);
}
