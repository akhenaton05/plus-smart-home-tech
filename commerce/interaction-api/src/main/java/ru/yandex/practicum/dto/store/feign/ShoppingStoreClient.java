package ru.yandex.practicum.dto.store.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.store.ProductDto;
import ru.yandex.practicum.dto.store.entity.ProductCategory;
import ru.yandex.practicum.dto.store.entity.QuantityState;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "shopping-store")
public interface ShoppingStoreClient {

    @GetMapping("/api/v1/shopping-store")
    @ResponseStatus(HttpStatus.OK)
    List<ProductDto> getProductsByCategory(@RequestParam("category") ProductCategory category,
                                           @RequestParam("page") int page,
                                           @RequestParam("size") int size,
                                           @RequestParam("sort") String sort);

    @GetMapping("/api/v1/shopping-store/{productId}")
    @ResponseStatus(HttpStatus.OK)
    ProductDto getProductsById(@PathVariable("productId") UUID productId);

    @PostMapping("/api/v1/shopping-store")
    @ResponseStatus(HttpStatus.OK)
    ProductDto updateProduct(@RequestBody ProductDto dto);

    @PutMapping("/api/v1/shopping-store")
    @ResponseStatus(HttpStatus.OK)
    ProductDto saveProduct(@RequestBody ProductDto dto);

    @PostMapping("/api/v1/shopping-store/removeProductFromStore")
    @ResponseStatus(HttpStatus.OK)
    Boolean removeProduct(@RequestBody UUID productId);

    @PostMapping("/api/v1/shopping-store/quantityState")
    @ResponseStatus(HttpStatus.OK)
    Boolean setQuantityState(@RequestParam("productId") UUID productId,
                             @RequestParam("quantityState") QuantityState quantityState);
}
