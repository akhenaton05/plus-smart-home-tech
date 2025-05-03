package ru.yandex.practicum.dto.order.feign;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderFeignConfig {

    @Bean
    public ErrorDecoder orderErrorDecoder() {
        return new OrderFeignErrorDecoder();
    }
}
