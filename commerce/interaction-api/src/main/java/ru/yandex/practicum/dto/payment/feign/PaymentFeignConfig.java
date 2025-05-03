package ru.yandex.practicum.dto.payment.feign;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentFeignConfig {

    @Bean
    public ErrorDecoder paymentErrorDecoder() {
        return new PaymentFeignErrorDecoder();
    }
}
