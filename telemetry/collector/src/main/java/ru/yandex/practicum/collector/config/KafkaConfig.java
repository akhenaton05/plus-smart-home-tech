package ru.yandex.practicum.collector.config;

import jakarta.validation.Valid;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.collector.event.TopicType;

import java.util.EnumMap;
import java.util.Properties;

@Getter
@Setter
@ToString
@Configuration
@ConfigurationProperties("collector.kafka")
public class KafkaConfig {
    private ProducerConfig producer;

    @Getter
    @Setter
    @ToString
    @ConfigurationProperties("collector.kafka.producer")
    public static class ProducerConfig {
        private final Properties properties;
        @Valid
        private final TopicConfig topics;
        private final EnumMap<TopicType, String> topicMap = new EnumMap<>(TopicType.class);

        public ProducerConfig(Properties properties, TopicConfig topics) {
            this.properties = properties;
            this.topics = topics;
            this.topicMap.put(TopicType.SENSOR_EVENTS, topics.getSensorsEvents());
            this.topicMap.put(TopicType.HUBS_EVENTS, topics.getHubsEvents());
        }

        public EnumMap<TopicType, String> getTopics() {
            return topicMap;
        }
    }
}