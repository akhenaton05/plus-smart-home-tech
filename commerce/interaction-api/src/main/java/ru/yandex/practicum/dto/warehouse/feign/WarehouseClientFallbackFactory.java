package ru.yandex.practicum.dto.warehouse.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.util.ServiceUnavailableException;
import ru.yandex.practicum.dto.cart.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.exception.NoSpecifiedProductInWarehouseException;

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
                log.error("Fallback triggered for WarehouseClient#checkProducts: Warehouse service is unavailable", cause);
                throw new ServiceUnavailableException("Warehouse service is unavailable during checkProducts");
            }

            @Override
            public AddressDto getWarehouseAddress() {
                return null;
            }

            @Override
            public void saveToWarehouse(NewProductInWarehouseRequest request) {

            }

            @Override
            public void addProducts(AddProductToWarehouseRequest request) {
                log.error("Fallback triggered for WarehouseClient#addProducts: Warehouse service is unavailable", cause);
                throw new ServiceUnavailableException("Warehouse service is unavailable during addProducts");
            }
        };
    }
}