package ru.yandex.practicum.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.dto.store.entity.ProductCategory;
import ru.yandex.practicum.dto.store.entity.ProductState;
import ru.yandex.practicum.dto.store.entity.QuantityState;

import java.util.UUID;

@Data
@Entity
@Table(name = "products")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "product_id")
    private UUID productId;

    @NotNull
    private String productName;

    @NotNull
    private String description;

    private String imageSrc;

    @NotNull
    @Enumerated(EnumType.STRING)
    private QuantityState quantityState;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ProductState productState;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ProductCategory productCategory;

    @NotNull
    @Min(1)
    private double price;
}
