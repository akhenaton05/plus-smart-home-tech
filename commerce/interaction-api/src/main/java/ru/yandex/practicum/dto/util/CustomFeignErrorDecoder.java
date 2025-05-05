package ru.yandex.practicum.dto.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.dto.cart.InsufficientProductInfo;
import ru.yandex.practicum.dto.store.exception.*;
import ru.yandex.practicum.dto.cart.exception.*;
import ru.yandex.practicum.dto.warehouse.exception.*;

import java.io.IOException;
import java.util.List;

@Slf4j
public class CustomFeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new ErrorDecoder.Default();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        log.info("Decoding Feign error for method: {}, status: {}", methodKey, response.status());
        try {
            String responseBody = response.body() != null
                    ? new String(response.body().asInputStream().readAllBytes())
                    : "{}";
            log.info("Response body: {}", responseBody);

            ErrorResponse errorResponse = objectMapper.readValue(responseBody, ErrorResponse.class);
            log.info("Parsed ErrorResponse: {}", errorResponse);

            switch (response.status()) {
                case 400:
                    if (errorResponse.getMessage().contains("Insufficient products in stock") ||
                            errorResponse.getUserMessage().contains("Недостаточно товаров на складе")) {
                        log.info("Throwing NoProductsInShoppingCartException");
                        return new NoProductsInShoppingCartException(
                                errorResponse.getMessage(),
                                errorResponse.getUserMessage(),
                                (List<InsufficientProductInfo>) errorResponse.getDetails()
                        );
                    } else if (errorResponse.getMessage().contains("Products is unavailable") ||
                            errorResponse.getUserMessage().contains("Нужных продуктов нет на складе")) {
                        log.info("Throwing NoSpecifiedProductInWarehouseException");
                        return new NoSpecifiedProductInWarehouseException(
                                errorResponse.getMessage(),
                                errorResponse.getUserMessage()
                        );
                    } else if (errorResponse.getMessage().contains("Insufficient quantity of products in stock") ||
                            errorResponse.getUserMessage().contains("Недостаточное кол-во товаров на складе")) {
                        log.info("Throwing ProductInShoppingCartLowQuantityInWarehouse");
                        return new ProductInShoppingCartLowQuantityInWarehouse(
                                errorResponse.getMessage(),
                                errorResponse.getUserMessage()
                        );
                    }
                    break;
                case 401:
                    log.info("Throwing NotAuthorizedUserException");
                    return new NotAuthorizedUserException(
                            errorResponse.getMessage(),
                            errorResponse.getUserMessage()
                    );
                case 404:
                    log.info("Throwing ProductNotFoundException");
                    return new ProductNotFoundException(
                            errorResponse.getMessage()
                    );
                default:
                    log.info("Throwing FeignClientException with status: {}", response.status());
                    return new FeignClientException(
                            response.status(),
                            errorResponse,
                            errorResponse.getMessage()
                    );
            }
        } catch (IOException e) {
            log.error("Failed to decode Feign error response: {}", e.getMessage(), e);
            return defaultErrorDecoder.decode(methodKey, response);
        }

        log.warn("Using default error decoder for method: {}, status: {}", methodKey, response.status());
        return defaultErrorDecoder.decode(methodKey, response);
    }
}
