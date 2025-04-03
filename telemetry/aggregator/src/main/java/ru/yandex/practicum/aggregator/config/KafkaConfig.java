package ru.yandex.practicum.aggregator.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
@ConfigurationProperties(prefix = "aggregator.kafka")
@Getter
@Setter
@ToString
public class KafkaConfig {
    private ConsumerConfig consumer;
    private ProducerConfig producer;

    @Getter
    @Setter
    @ToString
    public static class ConsumerConfig {
        private Properties properties;
        private String topic;
    }

    @Getter
    @Setter
    @ToString
    public static class ProducerConfig {
        private Properties properties;
        private String topic;
    }
}
