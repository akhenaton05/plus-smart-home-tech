package ru.yandex.practicum.mapper;

import lombok.Data;
import ru.yandex.practicum.dto.payment.dto.PaymentDto;
import ru.yandex.practicum.entity.Payment;

@Data
public class PaymentMapper {

    public static PaymentDto toDto(Payment payment) {
        return PaymentDto.builder()
                .paymentId(payment.getPaymentId())
                .totalPayment(payment.getTotalCost())
                .deliveryTotal(payment.getDeliveryCost())
                .feeTotal(payment.getTax())
                .build();
    }
}
