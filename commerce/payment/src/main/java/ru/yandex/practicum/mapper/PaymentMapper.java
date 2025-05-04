package ru.yandex.practicum.mapper;

import org.mapstruct.*;
import ru.yandex.practicum.dto.payment.dto.PaymentDto;
import ru.yandex.practicum.entity.Payment;


@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(source = "totalCost", target = "totalPayment")
    @Mapping(source = "deliveryCost", target = "deliveryTotal")
    @Mapping(source = "tax", target = "feeTotal")
    PaymentDto toDto(Payment payment);
}