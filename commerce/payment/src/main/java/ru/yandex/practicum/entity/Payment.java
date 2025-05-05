package ru.yandex.practicum.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.dto.payment.dto.PaymentState;

import java.util.UUID;

@Entity
@Table(name = "payments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID paymentId;

    private UUID orderId;

    private double productCost;
    private double deliveryCost;
    private double tax;
    private double totalCost;

    @Enumerated(EnumType.STRING)
    private PaymentState state;
}
