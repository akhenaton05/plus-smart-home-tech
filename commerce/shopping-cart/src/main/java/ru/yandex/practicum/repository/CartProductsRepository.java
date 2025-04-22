package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.entity.CartProducts;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartProductsRepository extends JpaRepository<CartProducts, UUID> {
    Optional<CartProducts> findByProductId(UUID productId);
}
