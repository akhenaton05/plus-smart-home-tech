package ru.yandex.practicum;

import net.devh.boot.grpc.client.autoconfigure.GrpcClientAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@Import(GrpcClientAutoConfiguration.class)
@EnableConfigurationProperties
@SpringBootApplication(scanBasePackages = "ru.yandex.practicum")
public class AnalyzerApplication {
    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(AnalyzerApplication.class, args);
    }
}