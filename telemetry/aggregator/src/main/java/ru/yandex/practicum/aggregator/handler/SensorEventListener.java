package ru.yandex.practicum.aggregator.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SensorEventListener {

    private final SnapshotAggregator snapshotAggregator;
    private final KafkaSnapshotProducer kafkaSnapshotProducer;

    @KafkaListener(topics = "${aggregator.kafka.consumer.topic}",
            groupId = "aggregator-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void handleEvent(SensorEventAvro event) {
        log.debug("Received event: {}", event);
        Optional<SensorsSnapshotAvro> updatedSnapshot = snapshotAggregator.updateState(event);
        updatedSnapshot.ifPresent(snapshot -> {
            log.info("Snapshot updated for hub {}: sending to Kafka", snapshot.getHubId());
            kafkaSnapshotProducer.send(snapshot);
        });
    }
}
