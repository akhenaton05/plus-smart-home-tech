package ru.yandex.practicum.dto.delivery.feign;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeliveryFeignConfig {

    @Bean
    public ErrorDecoder deliveryErrorDecoder() {
        return new DeliveryFeignErrorDecoder();
    }
}
