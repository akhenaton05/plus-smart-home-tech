package ru.yandex.practicum.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.order.dto.OrderDto;
import ru.yandex.practicum.dto.order.exception.NoOrderFoundException;
import ru.yandex.practicum.dto.order.feign.OrderClient;
import ru.yandex.practicum.dto.payment.dto.PaymentDto;
import ru.yandex.practicum.dto.payment.dto.PaymentState;
import ru.yandex.practicum.dto.payment.exception.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.dto.store.ProductDto;
import ru.yandex.practicum.dto.store.feign.ShoppingStoreClient;
import ru.yandex.practicum.entity.Payment;
import ru.yandex.practicum.mapper.PaymentMapper;
import ru.yandex.practicum.repository.PaymentRepository;

import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final ShoppingStoreClient storeClient;
    private final OrderClient orderClient;
    private final PaymentMapper paymentMapper;

    private static final double TAX_RATE = 0.10;
    private static final double FIXED_DELIVERY_COST = 50.0;

    @Override
    @Transactional(readOnly = true)
    public double calculateProductCost(OrderDto order) {
        validateOrder(order);
        log.info("Calculating product cost for order: {}", order.getOrderId());

        double productCost = 0;
        log.info("Fetching product prices for order: {}", order.getOrderId());
        for (UUID productId : order.getProducts().keySet()) {
            ProductDto dto = storeClient.getProductsById(productId);
            productCost += dto.getPrice() * order.getProducts().get(productId);
        }
        log.info("Product prices fetched successfully for order: {}", order.getOrderId());

        log.info("Product cost for order {}: {}", order.getOrderId(), productCost);
        return productCost;
    }

    @Override
    @Transactional(readOnly = true)
    public double calculateTotalCost(OrderDto dto) {
        validateOrder(dto);
        log.info("Calculating total cost for order: {}", dto.getOrderId());

        double productCost = calculateProductCost(dto);
        double tax = productCost * TAX_RATE;
        double totalCost = productCost + tax + FIXED_DELIVERY_COST;

        log.info("Total cost for order {}: {} (product: {}, tax: {}, delivery: {})",
                dto.getOrderId(), totalCost, productCost, tax, FIXED_DELIVERY_COST);

        return totalCost;
    }

    @Override
    @Transactional
    public void successPayment(UUID paymentId) {
        log.info("Processing payment success for payment ID: {}", paymentId);

        Payment payment = findPayment(paymentId);

        payment.setState(PaymentState.SUCCESS);
        payment = paymentRepository.save(payment);

        orderClient.orderPayment(payment.getOrderId());
        log.info("Order status updated to PAID for order ID: {}", payment.getOrderId());
    }

    @Override
    @Transactional
    public void failedPayment(UUID paymentId) {
        log.info("Processing payment fail for payment ID: {}", paymentId);

        Payment payment = findPayment(paymentId);

        payment.setState(PaymentState.FAILED);
        payment = paymentRepository.save(payment);

        orderClient.orderFailedPayment(payment.getOrderId());
        log.info("Order status updated to PAYMENT_FAILED for order ID: {}", payment.getOrderId());
    }

    @Override
    @Transactional
    public PaymentDto createPayment(OrderDto dto) {
        validateOrder(dto);

        log.info("Creating payment for order: {}", dto.getOrderId());

        double productCost = calculateProductCost(dto);
        double tax = productCost * TAX_RATE;
        double deliveryCost = dto.getDeliveryPrice();
        double totalCost = calculateTotalCost(dto);

        Payment payment = Payment.builder()
                .orderId(dto.getOrderId())
                .state(PaymentState.PENDING)
                .tax(tax)
                .productCost(productCost)
                .deliveryCost(deliveryCost)
                .totalCost(totalCost)
                .build();

        payment = paymentRepository.save(payment);
        log.info("Payment created with ID: {} for order: {}", payment.getPaymentId(), dto.getOrderId());

        return paymentMapper.toDto(payment);
    }

    private void validateOrder(OrderDto dto) {
        if (Objects.isNull(dto) || Objects.isNull(dto.getOrderId()) || Objects.isNull(dto.getProducts()) || dto.getProducts().isEmpty()) {
            throw new NotEnoughInfoInOrderToCalculateException(
                    "Order or products cannot be null or empty",
                    "Недостаточно информации в заказе для расчёта");
        }

        dto.getProducts().forEach((id, quantity) -> {
            if (quantity <= 0) {
                throw new NotEnoughInfoInOrderToCalculateException(
                        "Quantity must be positive for product ID: " + id,
                        "Количество товара должно быть положительным");
            }
        });
    }

    private Payment findPayment(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NoOrderFoundException(
                        "Payment not found for ID: " + paymentId,
                        "Оплата не найдена"));
    }
}
