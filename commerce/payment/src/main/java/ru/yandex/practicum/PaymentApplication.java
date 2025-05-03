package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableConfigurationProperties
@EnableFeignClients(basePackages = {"ru.yandex.practicum"})
public class PaymentApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentApplication.class);
    }
}