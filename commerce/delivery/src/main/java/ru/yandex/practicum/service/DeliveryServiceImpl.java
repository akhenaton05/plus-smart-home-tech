package ru.yandex.practicum.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.delivery.dto.DeliveryCost;
import ru.yandex.practicum.dto.delivery.dto.DeliveryDto;
import ru.yandex.practicum.dto.delivery.dto.DeliveryState;
import ru.yandex.practicum.dto.delivery.exception.NoDeliveryFoundException;
import ru.yandex.practicum.dto.order.dto.OrderDto;
import ru.yandex.practicum.dto.order.feign.OrderClient;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.ShippedToDeliveryRequest;
import ru.yandex.practicum.dto.warehouse.feign.WarehouseClient;
import ru.yandex.practicum.entity.Delivery;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.repository.DeliveryRepository;

import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final OrderClient orderClient;
    private final WarehouseClient warehouseClient;
    private final DeliveryMapper deliveryMapper;

    @Override
    @Transactional
    public DeliveryDto planDelivery(DeliveryDto dto) {
        log.info("Validating delivery {}", dto);
        validateDeliveryDto(dto);

        Delivery delivery = deliveryMapper.toEntity(dto);
        delivery.setState(DeliveryState.CREATED);

        log.info("Delivery created and saved {}", delivery);
        return deliveryMapper.toDto(deliveryRepository.save(delivery));
    }

    @Override
    @Transactional(readOnly = true)
    public double calculateDeliveryCost(OrderDto dto) {
        log.info("Sending request to warehouseClient for address");
        AddressDto warehouseAddress = warehouseClient.getWarehouseAddress();

        double cost = DeliveryCost.BASE_COST.getValue();

        // Учёт адреса склада
        String warehouseName = warehouseAddress.getStreet().toUpperCase();
        if (warehouseName.contains("ADDRESS_1")) {
            cost *= 1;
        } else if (warehouseName.contains("ADDRESS_2")) {
            cost *= 2;
            cost += DeliveryCost.BASE_COST.getValue();
        }

        if (dto.getFragile()) {
            cost += cost * DeliveryCost.FRAGILE_FACTOR.getValue();
        }

        cost += dto.getDeliveryWeight() * DeliveryCost.WEIGHT_FACTOR.getValue();
        cost += dto.getDeliveryVolume() * DeliveryCost.VOLUME_FACTOR.getValue();

        // Учёт адреса доставки
        Delivery delivery = findDelivery(dto.getOrderId());

        if (!delivery.getToAddress().getStreet().equalsIgnoreCase(warehouseAddress.getStreet())) {
            cost += cost * DeliveryCost.ADDRESS_FACTOR.getValue();
        }

        log.info("Delivery cost for order {}: {}", dto.getOrderId(), cost);
        return cost;
    }

    @Override
    public void successfulDelivery(UUID orderId) {
        log.info("Processing successful delivery for order ID: {}", orderId);
        Delivery delivery = findDelivery(orderId);

        delivery.setState(DeliveryState.DELIVERED);
        deliveryRepository.save(delivery);

        orderClient.orderCompleted(orderId);
        log.info("Order status updated for order ID: {}", orderId);
    }


    @Override
    public void failedDelivery(UUID orderId) {
        log.info("Processing failed delivery for order ID: {}", orderId);
        Delivery delivery = findDelivery(orderId);

        delivery.setState(DeliveryState.FAILED);
        deliveryRepository.save(delivery);

        orderClient.orderFailedDelivery(orderId);
        log.info("Order status updated for order ID: {}", orderId);
    }

    @Override
    public void pickedDelivery(UUID orderId) {
        log.info("Processing picked delivery for order ID: {}", orderId);
        Delivery delivery = findDelivery(orderId);

        delivery.setState(DeliveryState.IN_PROGRESS);
        log.info("Assembling products for delivery");
        orderClient.orderAssembly(orderId);

        log.info("Assigning delivery for warehouse");
        warehouseClient.shippedToDelivery(ShippedToDeliveryRequest.builder()
                .orderId(orderId)
                .deliveryId(delivery.getDeliveryId())
                .build());

        log.info("Delivery saved with status IN_PROGRESS");
        deliveryRepository.save(delivery);
    }

    private void validateDeliveryDto(DeliveryDto dto) {
        if (Objects.isNull(dto) || Objects.isNull(dto.getOrderId()) ||
                Objects.isNull(dto.getFromAddress()) || Objects.isNull(dto.getToAddress())) {
            throw new IllegalArgumentException("DeliveryDTO cant be null");
        }
    }

    private Delivery findDelivery(UUID orderId) {
        return deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NoDeliveryFoundException(
                        "Delivery not found for order ID: " + orderId,
                        "Доставка не найдена для заказа"));
    }
}
