package ru.yandex.practicum.dto.payment.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PaymentDto {
    private UUID paymentId;
    private Double totalPayment;
    private Double deliveryTotal;
    private Double feeTotal;
}
