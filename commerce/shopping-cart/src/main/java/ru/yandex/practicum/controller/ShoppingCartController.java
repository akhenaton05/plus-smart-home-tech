package ru.yandex.practicum.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.service.CartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/shopping-cart")
public class ShoppingCartController {
    private final CartService cartService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ShoppingCartDto getCart(@RequestParam String username) {
        log.info("New 'GET' request for username {}", username);
        return cartService.getCart(username);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public ShoppingCartDto saveCart(@RequestParam String username,
                                    @RequestBody Map<UUID, Long> cartProducts) {
        log.info("New 'PUT' create request for cart {} for username {}", cartProducts, username);
        return cartService.saveCart(username, cartProducts);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteCart(@RequestParam String username) {
        log.info("New 'DELETE' request for username {}", username);
        cartService.deleteCart(username);
    }

    @PostMapping("/remove")
    @ResponseStatus(HttpStatus.OK)
    public ShoppingCartDto removeProductCart(@RequestParam String username,
                                             @RequestBody List<UUID> productIds) {
        log.info("New 'POST' request for username {} to change {}", username, productIds);
        return cartService.removeProductCart(username, productIds);
    }

    @PostMapping("/change-quantity")
    @ResponseStatus(HttpStatus.OK)
    public ShoppingCartDto changeProductQuantity(@RequestParam String username,
                                                 @RequestBody ChangeProductQuantityRequest request) {
        log.info("New 'POST' request for username {} to change {} quantity", username, request);
        return cartService.changeProductQuantity(username, request);
    }
}
