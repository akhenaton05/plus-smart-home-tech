package ru.yandex.practicum.collector.service.handler;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.config.KafkaConfig;

import java.util.Objects;
import java.util.Properties;

@Component
@RequiredArgsConstructor
public class KafkaEventImpl implements KafkaClient {
    private final KafkaConfig kafkaConfig;
    private Producer<String, SpecificRecordBase> producer;

    @PostConstruct
    public void init() {

        if (Objects.isNull(kafkaConfig)) {
            throw new IllegalStateException("kafkaConfig не был внедрен Spring'ом!");
        }

        if (Objects.isNull(kafkaConfig.getProducer())) {
            throw new IllegalStateException("KafkaConfig.getProducer() вернул null");
        }

        Properties properties = new Properties();
        properties.putAll(kafkaConfig.getProducer().getProperties());
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ru.yandex.practicum.collector.serializer.AvroSerializer.class.getName());

        this.producer = new KafkaProducer<>(properties);
    }

    @Override
    public Producer<String, SpecificRecordBase> getProducer() {
        return producer;
    }

    @Override
    @PreDestroy
    public void stop() {
        if (Objects.nonNull(producer)) {
            producer.close();
        }
    }
}
