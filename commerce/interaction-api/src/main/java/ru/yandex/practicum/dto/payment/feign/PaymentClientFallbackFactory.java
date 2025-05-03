package ru.yandex.practicum.dto.payment.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.order.dto.OrderDto;
import ru.yandex.practicum.dto.order.exception.NoOrderFoundException;
import ru.yandex.practicum.dto.payment.dto.PaymentDto;
import ru.yandex.practicum.dto.payment.exception.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.dto.util.ServiceUnavailableException;

import java.util.UUID;

@Slf4j
@Component
public class PaymentClientFallbackFactory implements FallbackFactory<PaymentClient> {

    @Override
    public PaymentClient create(Throwable cause) {
        return new PaymentClient() {
            @Override
            public PaymentDto createPayment(OrderDto dto) {
                if (cause instanceof NotEnoughInfoInOrderToCalculateException) {
                    log.info("Re-throwing NotEnoughInfoInOrderToCalculateException in fallback: {}", cause.getMessage());
                    throw (NotEnoughInfoInOrderToCalculateException) cause;
                }
                log.error("Fallback triggered for PaymentClient#createPayment: Payment service is unavailable", cause);
                throw new ServiceUnavailableException("Payment service is unavailable during createPayment");
            }

            @Override
            public double calculateTotalCost(OrderDto dto) {
                if (cause instanceof NotEnoughInfoInOrderToCalculateException) {
                    log.info("Re-throwing NotEnoughInfoInOrderToCalculateException in fallback: {}", cause.getMessage());
                    throw (NotEnoughInfoInOrderToCalculateException) cause;
                }
                log.error("Fallback triggered for PaymentClient#calculateTotalCost: Payment service is unavailable", cause);
                throw new ServiceUnavailableException("Payment service is unavailable during calculateTotalCost");
            }

            @Override
            public double calculateProductCost(OrderDto dto) {
                if (cause instanceof NotEnoughInfoInOrderToCalculateException) {
                    log.info("Re-throwing NotEnoughInfoInOrderToCalculateException in fallback: {}", cause.getMessage());
                    throw (NotEnoughInfoInOrderToCalculateException) cause;
                }
                log.error("Fallback triggered for PaymentClient#calculateProductCost: Payment service is unavailable", cause);
                throw new ServiceUnavailableException("Payment service is unavailable during calculateProductCost");
            }

            @Override
            public void refund(UUID paymentId) {
                if (cause instanceof NoOrderFoundException) {
                    log.info("Re-throwing NoOrderFoundException in fallback: {}", cause.getMessage());
                    throw (NoOrderFoundException) cause;
                }
                log.error("Fallback triggered for PaymentClient#refund: Payment service is unavailable", cause);
                throw new ServiceUnavailableException("Payment service is unavailable during refund");
            }

            @Override
            public void failed(UUID paymentId) {
                if (cause instanceof NoOrderFoundException) {
                    log.info("Re-throwing NoOrderFoundException in fallback: {}", cause.getMessage());
                    throw (NoOrderFoundException) cause;
                }
                log.error("Fallback triggered for PaymentClient#failed: Payment service is unavailable", cause);
                throw new ServiceUnavailableException("Payment service is unavailable during failed");
            }
        };
    }
}
