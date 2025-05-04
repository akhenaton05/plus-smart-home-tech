package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.dto.delivery.dto.DeliveryDto;
import ru.yandex.practicum.entity.Delivery;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {
    DeliveryDto toDto(Delivery delivery);

    Delivery toEntity(DeliveryDto dto);
}
