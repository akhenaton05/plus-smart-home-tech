package ru.yandex.practicum.aggregator.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaSnapshotProducer {

    private final KafkaConfig kafkaConfig;
    private Producer<String, SpecificRecordBase> producer;

    @PostConstruct
    public void init() {
        producer = new KafkaProducer<>(kafkaConfig.getProducer().getProperties());
        log.info("Kafka producer initialized");
    }

    public void send(SpecificRecordBase snapshot) {
        String topic = kafkaConfig.getProducer().getTopic();
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(topic, null, snapshot);
        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                log.error("Failed to send snapshot to Kafka: {}", snapshot, exception);
            } else {
                log.debug("Snapshot sent to Kafka: partition={}, offset={}", metadata.partition(), metadata.offset());
            }
        });
    }

    public void flush() {
        producer.flush();
        log.info("Kafka producer flushed");
    }

    @PreDestroy
    public void close() {
        if (producer != null) {
            log.info("Closing Kafka producer...");
            producer.close();
            log.info("Kafka producer closed");
        }
    }
}
