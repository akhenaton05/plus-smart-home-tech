package ru.yandex.practicum.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.collector.config.KafkaConfig;
import ru.yandex.practicum.collector.event.TopicType;
import ru.yandex.practicum.collector.service.handler.KafkaClient;

import java.lang.reflect.InvocationTargetException;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaEventProducer {

    private final KafkaConfig kafkaConfig;

    private final KafkaClient client;

    public void send(TopicType topicType, SpecificRecordBase event) {
        log.trace("\nKafkaEventProducer: event {}", event);

        String topic = kafkaConfig.getProducer().getTopics().get(topicType);
        if (topic == null) {
            throw new IllegalArgumentException("Unknown topic type: " + topicType);
        }

        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(topic, null, event);
        log.trace("\nKafkaEventProducer: record {}, class {}", record,
                record.value() != null ? getPayloadClass(record.value()) : null);
        client.getProducer().send(record);
    }

    private String getPayloadClass(Object event) {
        try {
            Object payload = event.getClass().getMethod("getPayload").invoke(event);
            return payload != null ? payload.getClass().getSimpleName() : null;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }


}
