package ru.yandex.practicum.mapper;

import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.entity.Cart;
import ru.yandex.practicum.entity.CartProducts;

import java.util.stream.Collectors;

public class CartMapper{

    public static ShoppingCartDto toDto(Cart cart) {
        return ShoppingCartDto.builder()
                .shoppingCartId(cart.getId())
                .products(cart.getProducts().stream()
                        .collect(Collectors.toMap(CartProducts::getProductId, CartProducts::getQuantity)))
                .build();
    }
}
