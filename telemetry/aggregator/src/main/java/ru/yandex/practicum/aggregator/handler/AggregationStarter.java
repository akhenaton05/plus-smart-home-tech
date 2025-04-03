package ru.yandex.practicum.aggregator.handler;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.aggregator.config.KafkaConfig;
import ru.yandex.practicum.aggregator.config.KafkaSnapshotProducer;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;

/**
 * Класс AggregationStarter, ответственный за запуск агрегации данных.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {

    private final KafkaConfig kafkaConfig;
    private final SnapshotAggregator snapshotAggregator;
    private final KafkaSnapshotProducer kafkaSnapshotProducer;
    private Consumer<String, SensorEventAvro> consumer;

    public void start() {
        try {
            consumer = new KafkaConsumer<>(kafkaConfig.getConsumer().getProperties());
            consumer.subscribe(Collections.singletonList(kafkaConfig.getConsumer().getTopic()));
            log.info("Subscribed to topic: {}", kafkaConfig.getConsumer().getTopic());

            while (true) {
                ConsumerRecords<String, SensorEventAvro> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, SensorEventAvro> record : records) {
                    SensorEventAvro event = record.value();
                    log.debug("Received event: {}", event);

                    Optional<SensorsSnapshotAvro> updatedSnapshot = snapshotAggregator.updateState(event);
                    updatedSnapshot.ifPresent(snapshot -> {
                        log.info("Snapshot updated for hub {}: sending to Kafka", snapshot.getHubId());
                        kafkaSnapshotProducer.send(snapshot);
                    });
                }
                consumer.commitSync();
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {
            try {
                kafkaSnapshotProducer.flush();
                consumer.commitSync();
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
                log.info("Закрываем продюсер");
                kafkaSnapshotProducer.close();
            }
        }
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down AggregationStarter...");
        consumer.wakeup();
    }
}
