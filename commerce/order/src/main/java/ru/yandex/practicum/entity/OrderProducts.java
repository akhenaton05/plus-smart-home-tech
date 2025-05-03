package ru.yandex.practicum.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "order_products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderProducts {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @ToString.Exclude
    private Order order;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    private Long quantity;
}
