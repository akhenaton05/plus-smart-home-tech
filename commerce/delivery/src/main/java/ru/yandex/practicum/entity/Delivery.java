package ru.yandex.practicum.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.dto.delivery.dto.DeliveryState;

import java.util.UUID;

@Entity
@Table(name = "deliveries")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID deliveryId;

    private UUID orderId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "from_address_id", referencedColumnName = "id")
    private Address fromAddress;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "to_address_id", referencedColumnName = "id")
    private Address toAddress;

    @Enumerated(EnumType.STRING)
    private DeliveryState state;
}
