package ru.yandex.practicum.collector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.yandex.practicum.collector.config.KafkaConfig;

@SpringBootApplication
@EnableConfigurationProperties({KafkaConfig.class, KafkaConfig.ProducerConfig.class})
public class Collector {
    public static void main(String[] args) {
        SpringApplication.run(Collector.class, args);
    }
}
