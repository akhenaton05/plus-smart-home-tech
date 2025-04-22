package ru.yandex.practicum.dto.util;

//import feign.FeignException;
//import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.dto.cart.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.dto.cart.exception.NotAuthorizedUserException;
import ru.yandex.practicum.dto.store.exception.ProductNotFoundException;
import ru.yandex.practicum.dto.warehouse.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.dto.warehouse.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.dto.warehouse.exception.SpecifiedProductAlreadyInWarehouseException;

import java.net.ConnectException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<String> handleServiceUnavailableException(ServiceUnavailableException ex) {
        log.error("ServiceUnavailableException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFound(ProductNotFoundException ex) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, "Продукт не найден");
    }

    @ExceptionHandler(NoProductsInShoppingCartException.class)
    public ResponseEntity<ErrorResponse> handleNoProductInCart(NoProductsInShoppingCartException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, "Недостаточно товара на складе");
    }

    @ExceptionHandler(NotAuthorizedUserException.class)
    public ResponseEntity<ErrorResponse> handleNoAuthorisedUser(NotAuthorizedUserException ex) {
        return buildErrorResponse(ex, HttpStatus.UNAUTHORIZED, "Пользователь не авторизован");
    }

    @ExceptionHandler(SpecifiedProductAlreadyInWarehouseException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyExisted(SpecifiedProductAlreadyInWarehouseException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, "Такой товар уже есть на складе");
    }

    @ExceptionHandler(NoSpecifiedProductInWarehouseException.class)
    public ResponseEntity<ErrorResponse> handleNoSpecifiedProduct(NoSpecifiedProductInWarehouseException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, "Нет информации об этом товаре на складе");
    }

    @ExceptionHandler(ProductInShoppingCartLowQuantityInWarehouse.class)
    public ResponseEntity<ErrorResponse> handleLowQuantity(ProductInShoppingCartLowQuantityInWarehouse ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, "Нужного кол-во товара нету на складе");
    }

    @ExceptionHandler(FeignClientException.class)
    public ResponseEntity<ErrorResponse> handleFeignClientException(FeignClientException ex) {
        ErrorResponse errorResponse = ex.getErrorResponse();
        return ResponseEntity.status(ex.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера");
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(Throwable ex, HttpStatus status, String userMessage) {
        Object details = null;

        if (ex instanceof NoProductsInShoppingCartException e) {
            details = e.getDetails();
        }

        ErrorResponse response = ErrorResponse.builder()
                .httpStatus(status)
                .userMessage(userMessage)
                .message(ex.getMessage())
                .localizedMessage(ex.getLocalizedMessage())
                .stackTrace(convertStackTrace(ex.getStackTrace()))
                .suppressed(Arrays.stream(ex.getSuppressed())
                        .map(this::convertThrowable)
                        .collect(Collectors.toList()))
                .cause(convertThrowable(ex.getCause()))
                .details(details)
                .build();

//        log.error("Exception handled: {}", ex.getMessage(), ex);
        return ResponseEntity.status(status).body(response);
    }

    private List<StackTraceElementDto> convertStackTrace(StackTraceElement[] stackTrace) {
        return Arrays.stream(stackTrace)
                .map(el -> StackTraceElementDto.builder()
                        .classLoaderName(el.getClassLoaderName())
                        .moduleName(el.getModuleName())
                        .moduleVersion(el.getModuleVersion())
                        .methodName(el.getMethodName())
                        .fileName(el.getFileName())
                        .lineNumber(el.getLineNumber())
                        .className(el.getClassName())
                        .nativeMethod(el.isNativeMethod())
                        .build())
                .collect(Collectors.toList());
    }

    private ThrowableDto convertThrowable(Throwable throwable) {
        if (throwable == null) return null;
        return ThrowableDto.builder()
                .message(throwable.getMessage())
                .localizedMessage(throwable.getLocalizedMessage())
                .stackTrace(convertStackTrace(throwable.getStackTrace()))
                .build();
    }
}


