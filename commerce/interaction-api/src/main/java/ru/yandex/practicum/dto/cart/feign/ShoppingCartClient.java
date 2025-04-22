package ru.yandex.practicum.dto.cart.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.yandex.practicum.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "shopping-cart")
public interface ShoppingCartClient {

    @GetMapping("/api/v1/shopping-cart")
    @ResponseStatus(HttpStatus.OK)
    ShoppingCartDto getCart(@RequestParam("username") String username);

    @PutMapping("/api/v1/shopping-cart")
    @ResponseStatus(HttpStatus.OK)
    ShoppingCartDto saveCart(@RequestParam("username") String username,
                             @RequestBody Map<UUID, Long> cartProducts);

    @DeleteMapping("/api/v1/shopping-cart")
    @ResponseStatus(HttpStatus.OK)
    void deleteCart(@RequestParam("username") String username);

    @PostMapping("/api/v1/shopping-cart/remove")
    @ResponseStatus(HttpStatus.OK)
    ShoppingCartDto removeProductCart(@RequestParam("username") String username,
                                      @RequestBody List<UUID> productIds);

    @PostMapping("/api/v1/shopping-cart/change-quantity")
    @ResponseStatus(HttpStatus.OK)
    ShoppingCartDto changeProductQuantity(@RequestParam("username") String username,
                                          @RequestBody ChangeProductQuantityRequest request);
}
