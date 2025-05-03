package ru.yandex.practicum.dto.order.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.cart.exception.NotAuthorizedUserException;
import ru.yandex.practicum.dto.order.dto.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.dto.OrderDto;
import ru.yandex.practicum.dto.order.dto.ProductReturnRequest;
import ru.yandex.practicum.dto.order.exception.NoOrderFoundException;
import ru.yandex.practicum.dto.util.ServiceUnavailableException;
import ru.yandex.practicum.dto.warehouse.exception.NoSpecifiedProductInWarehouseException;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class OrderClientFallbackFactory implements FallbackFactory<OrderClient> {

    @Override
    public OrderClient create(Throwable cause) {
        return new OrderClient() {

            @Override
            public OrderDto createOrder(CreateNewOrderRequest request) {
                if (cause instanceof NoSpecifiedProductInWarehouseException) {
                    log.info("Re-throwing NoSpecifiedProductInWarehouseException in fallback: {}", cause.getMessage());
                    throw (NoSpecifiedProductInWarehouseException) cause;
                }
                log.error("Fallback triggered for OrderClient#checkProducts: Order service is unavailable", cause);
                throw new ServiceUnavailableException("Order service is unavailable during getUsersOrders");
            }

            @Override
            public List<OrderDto> getUsersOrders(String username) {
                if (cause instanceof NotAuthorizedUserException) {
                    log.info("Re-throwing NotAuthorizedUserException in fallback: {}", cause.getMessage());
                    throw (NotAuthorizedUserException) cause;
                }
                log.error("Fallback triggered for OrderClient#checkProducts: Order service is unavailable", cause);
                throw new ServiceUnavailableException("Order service is unavailable during getUsersOrders");
            }

            @Override
            public OrderDto returnOrder(ProductReturnRequest request) {
                if (cause instanceof NoOrderFoundException) {
                    log.info("Re-throwing NoOrderFoundException in fallback: {}", cause.getMessage());
                    throw (NoOrderFoundException) cause;
                }
                log.error("Fallback triggered for OrderClient#checkProducts: Order service is unavailable", cause);
                throw new ServiceUnavailableException("Order service is unavailable during getUsersOrders");
            }

            @Override
            public OrderDto orderPayment(UUID orderId) {
                if (cause instanceof NoOrderFoundException) {
                    log.info("Re-throwing NoOrderFoundException in fallback: {}", cause.getMessage());
                    throw (NoOrderFoundException) cause;
                }
                log.error("Fallback triggered for OrderClient#checkProducts: Order service is unavailable", cause);
                throw new ServiceUnavailableException("Order service is unavailable during getUsersOrders");
            }

            @Override
            public OrderDto orderFailedPayment(UUID orderId) {
                if (cause instanceof NoOrderFoundException) {
                    log.info("Re-throwing NoOrderFoundException in fallback: {}", cause.getMessage());
                    throw (NoOrderFoundException) cause;
                }
                log.error("Fallback triggered for OrderClient#checkProducts: Order service is unavailable", cause);
                throw new ServiceUnavailableException("Order service is unavailable during getUsersOrders");
            }

            @Override
            public OrderDto orderDelivery(UUID orderId) {
                if (cause instanceof NoOrderFoundException) {
                    log.info("Re-throwing NoOrderFoundException in fallback: {}", cause.getMessage());
                    throw (NoOrderFoundException) cause;
                }
                log.error("Fallback triggered for OrderClient#checkProducts: Order service is unavailable", cause);
                throw new ServiceUnavailableException("Order service is unavailable during getUsersOrders");
            }

            @Override
            public OrderDto orderFailedDelivery(UUID orderId) {
                if (cause instanceof NoOrderFoundException) {
                    log.info("Re-throwing NoOrderFoundException in fallback: {}", cause.getMessage());
                    throw (NoOrderFoundException) cause;
                }
                log.error("Fallback triggered for OrderClient#checkProducts: Order service is unavailable", cause);
                throw new ServiceUnavailableException("Order service is unavailable during getUsersOrders");
            }

            @Override
            public OrderDto orderCompleted(UUID orderId) {
                if (cause instanceof NoOrderFoundException) {
                    log.info("Re-throwing NoOrderFoundException in fallback: {}", cause.getMessage());
                    throw (NoOrderFoundException) cause;
                }
                log.error("Fallback triggered for OrderClient#checkProducts: Order service is unavailable", cause);
                throw new ServiceUnavailableException("Order service is unavailable during getUsersOrders");
            }

            @Override
            public OrderDto orderCalculate(UUID orderId) {
                if (cause instanceof NoOrderFoundException) {
                    log.info("Re-throwing NoOrderFoundException in fallback: {}", cause.getMessage());
                    throw (NoOrderFoundException) cause;
                }
                log.error("Fallback triggered for OrderClient#checkProducts: Order service is unavailable", cause);
                throw new ServiceUnavailableException("Order service is unavailable during getUsersOrders");
            }

            @Override
            public OrderDto orderCalculateDelivery(UUID orderId) {
                if (cause instanceof NoOrderFoundException) {
                    log.info("Re-throwing NoOrderFoundException in fallback: {}", cause.getMessage());
                    throw (NoOrderFoundException) cause;
                }
                log.error("Fallback triggered for OrderClient#checkProducts: Order service is unavailable", cause);
                throw new ServiceUnavailableException("Order service is unavailable during getUsersOrders");
            }

            @Override
            public OrderDto orderAssembly(UUID orderId) {
                if (cause instanceof NoOrderFoundException) {
                    log.info("Re-throwing NoOrderFoundException in fallback: {}", cause.getMessage());
                    throw (NoOrderFoundException) cause;
                }
                log.error("Fallback triggered for OrderClient#checkProducts: Order service is unavailable", cause);
                throw new ServiceUnavailableException("Order service is unavailable during getUsersOrders");
            }

            @Override
            public OrderDto orderFailedAssembly(UUID orderId) {
                if (cause instanceof NoOrderFoundException) {
                    log.info("Re-throwing NoOrderFoundException in fallback: {}", cause.getMessage());
                    throw (NoOrderFoundException) cause;
                }
                log.error("Fallback triggered for OrderClient#checkProducts: Order service is unavailable", cause);
                throw new ServiceUnavailableException("Order service is unavailable during getUsersOrders");
            }
        };
    }
}
