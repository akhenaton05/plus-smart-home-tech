package ru.yandex.practicum.aggregator.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.aggregator.config.KafkaProperties;


@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaSnapshotProducer {

    private final KafkaTemplate<String, SpecificRecordBase> kafkaTemplate;
    private final KafkaProperties kafkaProperties;

    public void send(SpecificRecordBase snapshot) {
        String topic = kafkaProperties.getProducer().getTopic();
        kafkaTemplate.send(topic, snapshot).whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to send snapshot to Kafka: {}", snapshot, ex);
            } else if (result != null) {
                log.debug("Snapshot sent to Kafka: partition={}, offset={}",
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }
}
