package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.store.ProductDto;
import ru.yandex.practicum.dto.store.entity.ProductCategory;
import ru.yandex.practicum.dto.store.entity.QuantityState;
import ru.yandex.practicum.service.StoreService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/shopping-store")
public class ShoppingStoreController {
    private final StoreService storeService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductDto> getProductsByCategory(@RequestParam ProductCategory category,
                                                  @RequestParam("page") int page,
                                                  @RequestParam("size") int size,
                                                  @RequestParam("sort") String sort) {
        log.info("New 'GET' request for category {}", category);
        return storeService.getProducts(category, page, size, sort);
    }

    @GetMapping("/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public ProductDto getProductsById(@PathVariable UUID productId) {
        log.info("New 'GET' request for productId {}", productId);
        return storeService.getProductsById(productId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ProductDto updateProduct(@Valid @RequestBody ProductDto dto) {
        log.info("New 'POST' update request for product {}", dto);
        return storeService.updateProduct(dto);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public ProductDto saveProduct(@Valid @RequestBody ProductDto dto) {
        log.info("New 'PUT' create request for product {}", dto);
        return storeService.saveProduct(dto);
    }

    @PostMapping("/removeProductFromStore")
    @ResponseStatus(HttpStatus.OK)
    public Boolean removeProduct(@RequestBody UUID productId) {
        log.info("New 'POST' request for removing product with id {}", productId);
        return storeService.removeProduct(productId);
    }

    @PostMapping("/quantityState")
    @ResponseStatus(HttpStatus.OK)
    public Boolean setQuantityState(@RequestParam UUID productId,
                                    @RequestParam QuantityState quantityState) {
        log.info("New 'POST' request for new quantity state {}", quantityState);
        return storeService.setQuantityState(productId, quantityState);
    }
}
