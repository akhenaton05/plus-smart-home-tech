package ru.yandex.practicum.dto.delivery.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.dto.delivery.exception.NoDeliveryFoundException;
import ru.yandex.practicum.dto.util.ErrorResponse;
import ru.yandex.practicum.dto.util.FeignClientException;

import java.io.IOException;

@Slf4j
public class DeliveryFeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();
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
                case 404:
                    log.info("Throwing NoDeliveryFoundException");
                    return new NoDeliveryFoundException(
                            errorResponse.getMessage(),
                            errorResponse.getUserMessage()
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
    }
}