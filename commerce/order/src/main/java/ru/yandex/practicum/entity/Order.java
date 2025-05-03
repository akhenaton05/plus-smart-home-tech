package ru.yandex.practicum.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.dto.order.dto.OrderState;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID orderId;

    private UUID shoppingCartId;
    private UUID paymentId;
    private UUID deliveryId;

    @Enumerated(EnumType.STRING)
    private OrderState state;

    private String username;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderProducts> products = new ArrayList<>();

    private Double deliveryWeight;
    private Double deliveryVolume;
    private Boolean fragile;
    private Double totalPrice;
    private Double deliveryPrice;
    private Double productPrice;
}
