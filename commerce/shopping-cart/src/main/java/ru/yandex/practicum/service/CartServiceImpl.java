package ru.yandex.practicum.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.cart.entity.CartState;
import ru.yandex.practicum.dto.util.ServiceUnavailableException;
import ru.yandex.practicum.dto.warehouse.feign.WarehouseClient;
import ru.yandex.practicum.dto.warehouse.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.entity.Cart;
import ru.yandex.practicum.entity.CartProducts;
import ru.yandex.practicum.dto.cart.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.dto.cart.exception.NotAuthorizedUserException;
import ru.yandex.practicum.mapper.CartMapper;
import ru.yandex.practicum.repository.CartRepository;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final WarehouseClient warehouseClient;

    @Override
    @Transactional(readOnly = true)
    public ShoppingCartDto getCart(String username) {
        return CartMapper.toDto(cartRepository.findByUsername(username)
                .orElseThrow(() -> new NotAuthorizedUserException("user with username " + username + " was not found", "Юзер не найден")));
    }

    @Override
    @Transactional
    public void deleteCart(String username) {
        Optional<Cart> cart = cartRepository.findByUsername(username);
        if (Objects.isNull(username) || cart.isEmpty()) {
            throw new NotAuthorizedUserException("user with username " + username + " was not found", "Юзер не найден");
        }
        cart.get().setCartState(CartState.DEACTIVATE);
    }

    @Override
    @Transactional
    public ShoppingCartDto removeProductCart(String username, List<UUID> productIds) {
        Cart cart = cartRepository.findByUsername(username)
                .orElseThrow(() -> new NotAuthorizedUserException("user with username " + username + " was not found", "Юзер не найден"));

        List<CartProducts> productsToRemove = cart.getProducts().stream()
                .filter(p -> productIds.contains(p.getProductId()))
                .toList();

        if (productsToRemove.isEmpty()) {
            throw new NoProductsInShoppingCartException("No matching products found in cart: " + productIds,
                    "Продукты не найдены в корзине: " + productIds);
        }

        List<UUID> missingProductIds = productIds.stream()
                .filter(id -> productsToRemove.stream()
                        .noneMatch(p -> p.getProductId().equals(id)))
                .toList();

        if (!missingProductIds.isEmpty()) {
            log.warn("Some products not found in cart for username {}: {}", username, missingProductIds);
        }

        cart.getProducts().removeAll(productsToRemove);

        return CartMapper.toDto(cartRepository.save(cart));
    }

    @Override
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        Cart cart = cartRepository.findByUsername(username)
                .orElseThrow(() -> new NotAuthorizedUserException("user with username " + username + " was not found", "Юзер не найден"));

        CartProducts product = cart.getProducts().stream()
                .filter(p -> p.getProductId().equals(request.getProductId()))
                .findFirst()
                .orElseThrow(() -> new NoProductsInShoppingCartException("Product not found in cart: " + request.getProductId(),
                        "Продукта нету в корзине"));

        product.setQuantity(request.getNewQuantity());

        return CartMapper.toDto(cartRepository.save(cart));
    }

    @Override
    @CircuitBreaker(name = "WarehouseClientcheckProductsShoppingCartDto", fallbackMethod = "fallbackSaveCart")
    @Transactional
    public ShoppingCartDto saveCart(String username, Map<UUID, Long> cartList) {
        if (Objects.isNull(username)) {
            throw new NotAuthorizedUserException("Username is null", "Юзер не найден");
        }

        if (cartList == null || cartList.isEmpty()) {
            throw new NoProductsInShoppingCartException("Cart list cannot be null or empty",
                    "Список продуктов не может быть пустым");
        }

        ShoppingCartDto tempCart = ShoppingCartDto.builder()
                .shoppingCartId(UUID.randomUUID())
                .products(cartList)
                .build();

        log.info("Calling warehouseClient.checkProducts...");
        warehouseClient.checkProducts(tempCart);
        log.info("warehouseClient.checkProducts completed");

        Cart cart = cartRepository.findByUsername(username)
                .orElseGet(() -> Cart.builder()
                        .username(username)
                        .products(new ArrayList<>())
                        .cartState(CartState.ACTIVE)
                        .build());

        for (Map.Entry<UUID, Long> entry : cartList.entrySet()) {
            UUID productId = entry.getKey();
            Long quantity = entry.getValue();
            if (quantity <= 0) {
                throw new NoSpecifiedProductInWarehouseException("Quantity must be positive for product: " + productId,
                        "Количество должно быть положительным");
            }

            CartProducts existingProduct = cart.getProducts().stream()
                    .filter(p -> p.getProductId().equals(productId))
                    .findFirst()
                    .orElse(null);

            if (existingProduct != null) {
                existingProduct.setQuantity(existingProduct.getQuantity() + quantity);
            } else {
                CartProducts cartProducts = CartProducts.builder()
                        .cart(cart)
                        .productId(productId)
                        .quantity(quantity)
                        .build();
                cart.getProducts().add(cartProducts);
            }
        }

        return CartMapper.toDto(cartRepository.save(cart));
    }

    public ShoppingCartDto fallbackSaveCart(String username, Map<UUID, Long> cartList, Throwable t) {
        log.error("Fallback Circuit для saveCart: {}, class: {}, {}", t.getMessage(), t.getClass(), t.getLocalizedMessage());
        if (t instanceof NoProductsInShoppingCartException) {
            throw (NoProductsInShoppingCartException) t;
        }
        if (t instanceof NoSpecifiedProductInWarehouseException) {
            throw (NoSpecifiedProductInWarehouseException) t;
        }
        throw new ServiceUnavailableException("Warehouse is unavailable");
    }
}
