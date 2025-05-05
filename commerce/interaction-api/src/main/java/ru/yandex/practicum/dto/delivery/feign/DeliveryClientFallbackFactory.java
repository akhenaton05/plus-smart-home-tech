package ru.yandex.practicum.dto.delivery.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.delivery.dto.DeliveryDto;
import ru.yandex.practicum.dto.delivery.exception.NoDeliveryFoundException;
import ru.yandex.practicum.dto.order.dto.OrderDto;
import ru.yandex.practicum.dto.util.ServiceUnavailableException;

import java.util.UUID;

@Component
@Slf4j
public class DeliveryClientFallbackFactory implements FallbackFactory<DeliveryClient> {

    @Override
    public DeliveryClient create(Throwable cause) {
        return new DeliveryClient() {

            @Override
            public DeliveryDto planDelivery(DeliveryDto dto) {
                return null;
            }

            @Override
            public double calculateDeliveryCost(OrderDto dto) {
                if (cause instanceof NoDeliveryFoundException) {
                    log.info("Re-throwing NoDeliveryFoundException in fallback: {}", cause.getMessage());
                    throw (NoDeliveryFoundException) cause;
                }
                log.error("Fallback triggered for DeliveryClient#calculateDeliveryCost: Delivery service is unavailable", cause);
                throw new ServiceUnavailableException("Delivery service is unavailable during calculateDeliveryCost");
            }

            @Override
            public void successfulDelivery(UUID orderId) {
                if (cause instanceof NoDeliveryFoundException) {
                    log.info("Re-throwing NoDeliveryFoundException in fallback: {}", cause.getMessage());
                    throw (NoDeliveryFoundException) cause;
                }
                log.error("Fallback triggered for DeliveryClient#successfulDelivery: Delivery service is unavailable", cause);
                throw new ServiceUnavailableException("Delivery service is unavailable during successfulDelivery");
            }

            @Override
            public void failedDelivery(UUID orderId) {
                if (cause instanceof NoDeliveryFoundException) {
                    log.info("Re-throwing NoDeliveryFoundException in fallback: {}", cause.getMessage());
                    throw (NoDeliveryFoundException) cause;
                }
                log.error("Fallback triggered for DeliveryClient#failedDelivery: Delivery service is unavailable", cause);
                throw new ServiceUnavailableException("Delivery service is unavailable during failedDelivery");
            }

            @Override
            public void pickedDelivery(UUID orderId) {
                if (cause instanceof NoDeliveryFoundException) {
                    log.info("Re-throwing NoDeliveryFoundException in fallback: {}", cause.getMessage());
                    throw (NoDeliveryFoundException) cause;
                }
                log.error("Fallback triggered for DeliveryClient#pickedDelivery: Delivery service is unavailable", cause);
                throw new ServiceUnavailableException("Delivery service is unavailable during pickedDelivery");
            }
        };
    }
}
