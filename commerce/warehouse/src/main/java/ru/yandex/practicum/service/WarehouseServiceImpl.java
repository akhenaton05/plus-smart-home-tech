package ru.yandex.practicum.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.cart.InsufficientProductInfo;
import ru.yandex.practicum.dto.cart.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.dto.order.exception.NoOrderFoundException;
import ru.yandex.practicum.dto.warehouse.*;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.entity.OrderBooking;
import ru.yandex.practicum.entity.Product;
import ru.yandex.practicum.dto.warehouse.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.dto.warehouse.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.mapper.WarehouseMapper;
import ru.yandex.practicum.repository.OrderBookingRepository;
import ru.yandex.practicum.repository.WarehouseRepository;

import java.security.SecureRandom;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final OrderBookingRepository orderBookingRepository;
    private final WarehouseMapper warehouseMapper;

    private static final String[] ADDRESSES = new String[]{"ADDRESS_1", "ADDRESS_2"};
    private static final String CURRENT_ADDRESS = ADDRESSES[Random.from(new SecureRandom()).nextInt(0, 2)];

    @Override
    @Transactional(readOnly = true)
    public BookedProductsDto checkProducts(ShoppingCartDto dto) {
        List<Product> availableProducts = warehouseRepository.findByProductIdIn(dto.getProducts().keySet());

        if (availableProducts.isEmpty()) {
            log.info("NoSpecifiedProductInWarehouseException");
            throw new NoSpecifiedProductInWarehouseException(
                    "Products is unavailable",
                    "Нужных продуктов нет на складе"
            );
        }

        Map<UUID, Product> productMap = availableProducts.stream()
                .collect(Collectors.toMap(Product::getProductId, Function.identity()));

        List<InsufficientProductInfo> insufficient = dto.getProducts().keySet().stream()
                .map(productId -> {
                    long requestedQty = dto.getProducts().get(productId);
                    Product product = productMap.get(productId);
                    long availableQty = product != null ? product.getQuantity() : 0;

                    if (availableQty < requestedQty) {
                        return new InsufficientProductInfo(productId, requestedQty - availableQty);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toList();

        if (!insufficient.isEmpty()) {
            log.info("NoProductsInShoppingCartException");
            throw new NoProductsInShoppingCartException(
                    "Insufficient products in stock",
                    "Недостаточно товаров на складе",
                    insufficient
            );
        }
        log.info("check completed");
        return convert(availableProducts);
    }

    @Override
    @Transactional
    public void saveToWarehouse(NewProductInWarehouseRequest request) {
        Product product = warehouseMapper.requestToProduct(request);
        if (warehouseRepository.findByProductId(product.getProductId()).isPresent()) {
            throw new SpecifiedProductAlreadyInWarehouseException("Products is already exists", "Продукт уже зарегистрирован на складе");
        }
        warehouseRepository.save(product);
    }

    @Override
    @Transactional
    public void addProductQuantity(AddProductToWarehouseRequest request) {
        Product product = warehouseRepository.findByProductId(request.getProductId())
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException("Product not found in warehouse", "Продукта нету на складе"));
        product.setQuantity(product.getQuantity() + request.getQuantity());
        log.info("Products quantity was updated {}", product);
        warehouseRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public AddressDto getWarehouseAddress() {
        AddressDto address = AddressDto.builder()
                .country(CURRENT_ADDRESS)
                .city(CURRENT_ADDRESS)
                .street(CURRENT_ADDRESS)
                .house(CURRENT_ADDRESS)
                .flat(CURRENT_ADDRESS)
                .build();
        log.info("Returned warehouse address: {}", CURRENT_ADDRESS);
        return address;
    }

    @Override
    @Transactional
    public BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request) {
        List<Product> availableProducts = warehouseRepository.findByProductIdIn(request.getProducts().keySet());

        if (availableProducts.isEmpty()) {
            log.info("NoSpecifiedProductInWarehouseException");
            throw new NoSpecifiedProductInWarehouseException(
                    "Products is unavailable",
                    "Нужных продуктов нет на складе"
            );
        }

        Map<UUID, Product> productMap = availableProducts.stream()
                .collect(Collectors.toMap(Product::getProductId, Function.identity()));

        List<InsufficientProductInfo> insufficient = request.getProducts().keySet().stream()
                .map(productId -> {
                    long requestedQty = request.getProducts().get(productId);
                    Product product = productMap.get(productId);
                    long availableQty = product != null ? product.getQuantity() : 0;

                    if (availableQty < requestedQty) {
                        return new InsufficientProductInfo(productId, requestedQty - availableQty);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toList();

        if (!insufficient.isEmpty()) {
            log.info("ProductInShoppingCartLowQuantityInWarehouse");
            throw new ProductInShoppingCartLowQuantityInWarehouse(
                    "Insufficient quantity of products in stock",
                    "Недостаточное кол-во товаров на складе",
                    insufficient
            );
        }
        log.info("Check completed");

        for (Map.Entry<UUID, Long> entry : request.getProducts().entrySet()) {
            Product product = productMap.get(entry.getKey());
            product.setQuantity(product.getQuantity() - entry.getValue());
            warehouseRepository.save(product);
        }

        OrderBooking orderBooking = OrderBooking.builder()
                .orderId(request.getOrderId())
                .products(request.getProducts())
                .build();

        orderBookingRepository.save(orderBooking);

        log.info("Order {} assembled and booked", request.getOrderId());
        return convert(availableProducts);
    }

    @Override
    @Transactional
    public void acceptReturn(Map<UUID, Long> returnProducts) {
        for (Map.Entry<UUID, Long> entry : returnProducts.entrySet()) {
            UUID productId = entry.getKey();
            Long returnQuantity = entry.getValue();

            Product product = warehouseRepository.findByProductId(productId)
                    .orElseThrow(() -> new NoSpecifiedProductInWarehouseException(
                            "Product not found in warehouse: " + productId,
                            "Продукта нету на складе"
                    ));

            product.setQuantity(product.getQuantity() + returnQuantity);
            warehouseRepository.save(product);
            log.info("Returned {} units of product {}", returnQuantity, productId);
        }
    }

    @Override
    @Transactional
    public void shippedToDelivery(ShippedToDeliveryRequest request) {
        OrderBooking booking = orderBookingRepository.findByOrderId(request.getOrderId())
                .orElseThrow(() -> new NoOrderFoundException(
                        "Order booking not found by order ID",
                        "Бронирование заказа не найдено"
                ));

        booking.setDeliveryId(request.getDeliveryId());
        orderBookingRepository.save(booking);
        log.info("Order {} linked with delivery ID: {}", request.getOrderId(), request.getDeliveryId());
    }

    private BookedProductsDto convert(List<Product> cartList) {
        boolean isFragile = false;
        double weight = 0;
        double volume = 0;
        for (Product p : cartList) {
            if (p.getIsFragile()) {
                isFragile = true;
            }

            long quantity = p.getQuantity();
            weight += p.getWeight() * quantity;
            volume += p.getWeight() * p.getDepth() * p.getWidth() * quantity;
        }
        log.info("Converted successfully");

        return BookedProductsDto.builder()
                .deliveryVolume(volume)
                .deliveryWeight(weight)
                .fragile(isFragile)
                .build();
    }
}
