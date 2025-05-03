package ru.yandex.practicum.mapper;

import ru.yandex.practicum.dto.delivery.dto.DeliveryDto;
import ru.yandex.practicum.dto.delivery.dto.DeliveryState;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.entity.Address;
import ru.yandex.practicum.entity.Delivery;

public class DeliveryMapper {

    public static DeliveryDto toDto(Delivery delivery) {
        return DeliveryDto.builder()
                .deliveryId(delivery.getDeliveryId())
                .orderId(delivery.getOrderId())
                .fromAddress(toAddressDto(delivery.getFromAddress()))
                .toAddress(toAddressDto(delivery.getToAddress()))
                .build();
    }

    public static Delivery toEntity(DeliveryDto dto) {
        return Delivery.builder()
                .deliveryId(dto.getDeliveryId())
                .orderId(dto.getOrderId())
                .toAddress(toAddressEntity(dto.getToAddress()))
                .fromAddress(toAddressEntity(dto.getFromAddress()))
                .state(DeliveryState.CREATED)
                .build();
    }

    public static AddressDto toAddressDto(Address address) {
        return AddressDto.builder()
                .country(address.getCountry())
                .city(address.getCity())
                .street(address.getStreet())
                .house(address.getHouse())
                .flat(address.getFlat())
                .build();
    }

    public static Address toAddressEntity(AddressDto dto) {
        return Address.builder()
                .country(dto.getCountry())
                .city(dto.getCity())
                .street(dto.getStreet())
                .house(dto.getHouse())
                .flat(dto.getFlat())
                .build();
    }
}
