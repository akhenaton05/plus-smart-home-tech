package ru.yandex.practicum.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.cart.exception.NotAuthorizedUserException;
import ru.yandex.practicum.dto.delivery.dto.DeliveryDto;
import ru.yandex.practicum.dto.delivery.feign.DeliveryClient;
import ru.yandex.practicum.dto.order.dto.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.dto.OrderDto;
import ru.yandex.practicum.dto.order.dto.OrderState;
import ru.yandex.practicum.dto.order.dto.ProductReturnRequest;
import ru.yandex.practicum.dto.order.exception.NoOrderFoundException;
import ru.yandex.practicum.dto.payment.dto.PaymentDto;
import ru.yandex.practicum.dto.payment.feign.PaymentClient;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.dto.warehouse.feign.WarehouseClient;
import ru.yandex.practicum.entity.Order;
import ru.yandex.practicum.entity.OrderProducts;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.repository.OrderRepository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final WarehouseClient warehouseClient;
    private final DeliveryClient deliveryClient;
    private final PaymentClient paymentClient;

    @Override
    @Transactional
    public OrderDto createOrder(CreateNewOrderRequest request) {
        log.info("Creating new order request {}", request);
        log.info("Calling warehouseClient.checkProducts");
        BookedProductsDto bookedProducts = warehouseClient.checkProducts(request.getShoppingCart());
        log.info("warehouseClient.checkProducts completed");

        Order order = Order.builder()
                .state(OrderState.NEW)
                .products(new ArrayList<>())
                .shoppingCartId(request.getShoppingCart().getShoppingCartId())
                .deliveryVolume(bookedProducts.getDeliveryVolume())
                .deliveryWeight(bookedProducts.getDeliveryWeight())
                .fragile(bookedProducts.isFragile())
                .build();

        //Заполняем orderProducts, сохраняем каскадно
        for (Map.Entry<UUID, Long> entry : request.getShoppingCart().getProducts().entrySet()) {
            OrderProducts orderProducts = OrderProducts.builder()
                    .order(order)
                    .productId(entry.getKey())
                    .quantity(entry.getValue())
                    .build();

            order.getProducts().add(orderProducts);
        }

        //Сохраняем для генерации orderId
        order = orderRepository.save(order);

        //Добавляем информацию о доставке
        log.info("Creating delivery for order");
        AddressDto warehouseAddressDto = warehouseClient.getWarehouseAddress();
        DeliveryDto deliveryDto = DeliveryDto.builder()
                .toAddress(request.getDeliveryAddress())
                .fromAddress(warehouseAddressDto)
                .orderId(order.getOrderId())
                .build();

        log.info("Planing delivery");
        DeliveryDto deliveryResponse =  deliveryClient.planDelivery(deliveryDto);

        order.setDeliveryId(deliveryResponse.getDeliveryId());
        log.info("Sending delivery price calculation request");
        order.setDeliveryPrice(deliveryClient.calculateDeliveryCost(OrderMapper.toDto(order)));

        //Добавляем информацию об оплате
        log.info("Sending product price calculation request {}", OrderMapper.toDto(order));
        double productPrice = paymentClient.calculateProductCost(OrderMapper.toDto(order));
        order.setProductPrice(productPrice);

        PaymentDto payment = paymentClient.createPayment(OrderMapper.toDto(order));
        order.setPaymentId(payment.getPaymentId());
        order.setTotalPrice(payment.getTotalPayment());

        orderRepository.save(order);
        log.info("Order {} created", order);
        return OrderMapper.toDto(order);
    }

    @Override
    public List<OrderDto> getUsersOrders(String username) {
        if (Objects.isNull(username)) {
            throw new NotAuthorizedUserException("User is null", "Поле юзер пустое");
        }

        List<Order> orders = orderRepository.findByUsername(username);

        log.info("Found {} orders for username: {}", orders.size(), username);
        return orders.stream()
                .map(OrderMapper::toDto)
                .toList();
    }

    @Override
    public OrderDto returnOrder(ProductReturnRequest request) {
        log.info("Got {} request for returning products",  request);
        Order order = findOrder(request.getOrderId());

        log.info("Sending warehouse return request");
        warehouseClient.returnProducts(request.getProducts());

        order.setState(OrderState.PRODUCT_RETURNED);
        order = orderRepository.save(order);
        log.info("Products returned successfully");
        return OrderMapper.toDto(order);
    }

    @Override
    public OrderDto orderPayment(UUID orderId) {
        log.info("got successful payment event");
        Order order = findOrder(orderId);

        order.setState(OrderState.PAID);
        order = orderRepository.save(order);
        log.info("Order status updated to PAID");
        return OrderMapper.toDto(order);
    }

    @Override
    public OrderDto orderFailedPayment(UUID orderId) {
        log.info("got failed payment event");
        Order order = findOrder(orderId);

        order.setState(OrderState.PAYMENT_FAILED);
        order = orderRepository.save(order);
        log.info("Order status updated to PAYMENT_FAILED");
        return OrderMapper.toDto(order);
    }

    @Override
    public OrderDto orderDelivery(UUID orderId) {
        log.info("Got order delivered event");
        Order order = findOrder(orderId);

        order.setState(OrderState.DELIVERED);
        order = orderRepository.save(order);
        log.info("Order status updated to DELIVERED");
        return OrderMapper.toDto(order);
    }

    @Override
    public OrderDto orderFailedDelivery(UUID orderId) {
        log.info("Got order failed event");
        Order order = findOrder(orderId);

        order.setState(OrderState.DELIVERY_FAILED);
        order = orderRepository.save(order);
        log.info("Order status updated to DELIVERY_FAILED");
        return OrderMapper.toDto(order);
    }

    @Override
    public OrderDto orderCompleted(UUID orderId) {
        log.info("Got order completed event");
        Order order = findOrder(orderId);

        order.setState(OrderState.COMPLETED);
        order = orderRepository.save(order);
        log.info("Order status updated to COMPLETED");
        return OrderMapper.toDto(order);
    }

    @Override
    public OrderDto orderCalculate(UUID orderId) {
        log.info("Got order calculation cost request");
        Order order = findOrder(orderId);

        paymentClient.calculateTotalCost(OrderMapper.toDto(order));

        order.setTotalPrice(paymentClient.calculateTotalCost(OrderMapper.toDto(order)));
        order = orderRepository.save(order);
        log.info("Order cost was calculated");
        return OrderMapper.toDto(order);
    }

    @Override
    public OrderDto orderCalculateDelivery(UUID orderId) {
        log.info("Got order delivery cost request");
        Order order = findOrder(orderId);

        order.setDeliveryPrice(deliveryClient.calculateDeliveryCost(OrderMapper.toDto(order)));
        order = orderRepository.save(order);
        log.info("Order delivery was calculated");
        return OrderMapper.toDto(order);
    }

    @Override
    public OrderDto orderAssembly(UUID orderId) {
        log.info("Got order request for assembling");
        Order order = findOrder(orderId);

        log.info("Sending return request to warehouse");
        warehouseClient.assemblyProductsForOrder(AssemblyProductsForOrderRequest.builder()
                        .products(productsListToMap(order.getProducts()))
                        .orderId(orderId)
                        .build());

        order.setState(OrderState.ASSEMBLED);
        order = orderRepository.save(order);
        log.info("Order status updated to ASSEMBLED");
        return OrderMapper.toDto(order);
    }

    @Override
    public OrderDto orderFailedAssembly(UUID orderId) {
        log.info("Got failed assembled order request");
        Order order = findOrder(orderId);

        order.setState(OrderState.ASSEMBLY_FAILED);
        order = orderRepository.save(order);
        log.info("Order status updated to ASSEMBLY_FAILED");
        return OrderMapper.toDto(order);
    }

    private Order findOrder(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Order not found", "Заказ не найден"));
    }

    private Map<UUID, Long> productsListToMap(List<OrderProducts> products) {
        return products.stream()
                .collect(Collectors.toMap(
                        OrderProducts::getProductId,
                        OrderProducts::getQuantity
                ));
    }
}
