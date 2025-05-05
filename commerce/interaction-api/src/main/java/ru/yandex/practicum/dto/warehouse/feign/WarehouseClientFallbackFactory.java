package ru.yandex.practicum.dto.warehouse.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.order.exception.NoOrderFoundException;
import ru.yandex.practicum.dto.util.ServiceUnavailableException;
import ru.yandex.practicum.dto.cart.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.dto.warehouse.*;
import ru.yandex.practicum.dto.warehouse.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.dto.warehouse.exception.ProductInShoppingCartLowQuantityInWarehouse;

import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
public class WarehouseClientFallbackFactory implements FallbackFactory<WarehouseClient> {

    @Override
    public WarehouseClient create(Throwable cause) {
        return new WarehouseClient() {
            @Override
            public BookedProductsDto checkProducts(ShoppingCartDto cart) {
                if (cause instanceof NoProductsInShoppingCartException) {
                    log.info("Re-throwing NoProductsInShoppingCartException in fallback: {}", cause.getMessage());
                    throw (NoProductsInShoppingCartException) cause;
                }
                if (cause instanceof NoSpecifiedProductInWarehouseException) {
                    log.info("Re-throwing NoSpecifiedProductInWarehouseException in fallback: {}", cause.getMessage());
                    throw (NoSpecifiedProductInWarehouseException) cause;
                }
                if (cause instanceof ProductInShoppingCartLowQuantityInWarehouse) {
                    log.info("Re-throwing ProductInShoppingCartLowQuantityInWarehouse in fallback: {}", cause.getMessage());
                    throw (ProductInShoppingCartLowQuantityInWarehouse) cause;
                }
                log.error("Fallback triggered for WarehouseClient#checkProducts: Warehouse service is unavailable", cause);
                throw new ServiceUnavailableException("Warehouse service is unavailable during checkProducts");
            }

            @Override
            public AddressDto getWarehouseAddress() {
                log.error("Fallback triggered for WarehouseClient#checkProducts: Warehouse service is unavailable", cause);
                throw new ServiceUnavailableException("Warehouse service is unavailable during checkProducts");
            }

            @Override
            public void saveToWarehouse(NewProductInWarehouseRequest request) {
                log.error("Fallback triggered for WarehouseClient#checkProducts: Warehouse service is unavailable", cause);
                throw new ServiceUnavailableException("Warehouse service is unavailable during checkProducts");
            }

            @Override
            public void addProducts(AddProductToWarehouseRequest request) {
                log.error("Fallback triggered for WarehouseClient#addProducts: Warehouse service is unavailable", cause);
                throw new ServiceUnavailableException("Warehouse service is unavailable during addProducts");
            }

            @Override
            public BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request) {
                if (cause instanceof NoProductsInShoppingCartException) {
                    log.info("Re-throwing NoProductsInShoppingCartException in fallback: {}", cause.getMessage());
                    throw (NoProductsInShoppingCartException) cause;
                }
                if (cause instanceof NoSpecifiedProductInWarehouseException) {
                    log.info("Re-throwing NoSpecifiedProductInWarehouseException in fallback: {}", cause.getMessage());
                    throw (NoSpecifiedProductInWarehouseException) cause;
                }
                if (cause instanceof ProductInShoppingCartLowQuantityInWarehouse) {
                    log.info("Re-throwing ProductInShoppingCartLowQuantityInWarehouse in fallback: {}", cause.getMessage());
                    throw (ProductInShoppingCartLowQuantityInWarehouse) cause;
                }
                log.error("Fallback triggered for WarehouseClient#checkProducts: Warehouse service is unavailable", cause);
                throw new ServiceUnavailableException("Warehouse service is unavailable during checkProducts");
            }

            @Override
            public void shippedToDelivery(ShippedToDeliveryRequest request) {
                if (cause instanceof NoOrderFoundException) {
                    log.info("Re-throwing NoOrderFoundException in fallback: {}", cause.getMessage());
                    throw (NoOrderFoundException) cause;
                }
                log.error("Fallback triggered for WarehouseClient#shippedToDelivery: Warehouse service is unavailable", cause);
                throw new ServiceUnavailableException("Warehouse service is unavailable during shippedToDelivery");
            }

            @Override
            public void returnProducts(Map<UUID, Long> returnProducts) {
                if (cause instanceof NoSpecifiedProductInWarehouseException) {
                    log.info("Re-throwing NoSpecifiedProductInWarehouseException in fallback: {}", cause.getMessage());
                    throw (NoSpecifiedProductInWarehouseException) cause;
                }
                log.error("Fallback triggered for WarehouseClient#checkProducts: Warehouse service is unavailable", cause);
                throw new ServiceUnavailableException("Warehouse service is unavailable during checkProducts");
            }
        };
    }
}