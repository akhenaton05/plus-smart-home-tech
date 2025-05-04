package ru.yandex.practicum.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.store.ProductDto;
import ru.yandex.practicum.dto.store.entity.ProductCategory;
import ru.yandex.practicum.dto.store.entity.ProductState;
import ru.yandex.practicum.dto.store.entity.QuantityState;
import ru.yandex.practicum.entity.Product;
import ru.yandex.practicum.dto.store.exception.ProductNotFoundException;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.repository.ProductRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class StoreServiceImpl implements StoreService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public List<ProductDto> getProducts(ProductCategory productCategory, int page, int size, String sort) {
        log.info("Fetching products with category: {}, page: {}, size: {}, sort: {}",
                productCategory, page, size, sort);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<Product> products = productCategory != null
                ? productRepository.findByProductCategory(productCategory, pageable)
                : productRepository.findAll(pageable);

        return products.getContent().stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDto getProductsById(UUID id) {
        return productRepository.findById(id)
                .map(productMapper::toDto)
                .orElseThrow(() -> new ProductNotFoundException(
                        "Product with ID " + id + " not found"
                ));
    }

    @Override
    public ProductDto saveProduct(ProductDto dto) {
        Product product = productMapper.dtoToProduct(dto);
        return productMapper.toDto(productRepository.save(product));
    }

    @Override
    public ProductDto updateProduct(ProductDto dto) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(
                        "Product with ID " + dto.getProductId() + " not found"));
        update(product, dto);
        return productMapper.toDto(productRepository.save(product));
    }

    @Override
    public Boolean removeProduct(UUID id) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setProductState(ProductState.DEACTIVATE);
                    productRepository.save(product);
                    return true;
                })
                .orElseThrow(() -> new ProductNotFoundException(
                        "Product with ID " + id + " not found"));
    }

    @Override
    public Boolean setQuantityState(UUID productId, QuantityState quantityState) {
        return productRepository.findById(productId)
                .map(product -> {
                    product.setQuantityState(quantityState);
                    productRepository.save(product);
                    return true;
                })
                .orElseThrow(() -> new ProductNotFoundException(
                        "Product with ID " + productId + " not found"));
    }

    private void update(Product product, ProductDto dto) {
        product.setProductName(dto.getProductName());
        product.setDescription(dto.getDescription());
        product.setImageSrc(dto.getImageSrc());
        product.setQuantityState(dto.getQuantityState());
        product.setProductCategory(dto.getProductCategory());
        product.setPrice(dto.getPrice());
    }
}
