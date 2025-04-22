package ru.yandex.practicum.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@Table(name = "warehouse_products")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    private UUID productId;

    private Double width;

    private Double height;

    private Double depth;

    private Double weight;

    @Column(name = "is_fragile")
    private Boolean isFragile;

    private Long quantity;
}
