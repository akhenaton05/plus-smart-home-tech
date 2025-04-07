package ru.yandex.practicum.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Properties;

@Configuration
@ConfigurationProperties(prefix = "analyzer.kafka")
@Getter
@Setter
@ToString
public class KafkaConfig {
    private ConsumerConfig hubConsumer;
    private ConsumerConfig snapshotConsumer;

    @Getter
    @Setter
    @ToString
    public static class ConsumerConfig {
        private Properties properties;
        private String topic;
    }

    @Bean
    public KafkaConsumer<String, HubEventAvro> hubConsumer() {
        Properties props = hubConsumer.getProperties();
        return new KafkaConsumer<>(props);
    }

    @Bean
    public KafkaConsumer<String, SensorsSnapshotAvro> snapshotConsumer() {
        Properties props = snapshotConsumer.getProperties();
        return new KafkaConsumer<>(props);
    }
}
