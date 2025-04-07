package ru.yandex.practicum.analyzer;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.config.KafkaConfig;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.service.SnapshotServiceImpl;

import java.time.Duration;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor {

    private final KafkaConfig kafkaConfig;
    private final KafkaConsumer<String, SensorsSnapshotAvro> consumer;
    private final SnapshotServiceImpl snapshotService;
    private volatile boolean running = true;

    public void start() {
        try {
            consumer.subscribe(Collections.singletonList(kafkaConfig.getSnapshotConsumer().getTopic()));
            log.info("Subscribed to topic: {}", kafkaConfig.getSnapshotConsumer().getTopic());

            while (running) {
                ConsumerRecords<String, SensorsSnapshotAvro> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    SensorsSnapshotAvro snapshot = record.value();
                    log.debug("Received event: {}", snapshot);

                    snapshotService.processSnapshot(snapshot);
                }
                consumer.commitSync();
            }
        } catch (WakeupException ignored) {
            log.info("Received WakeupException, shutting down...");
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {
            log.info("Закрываем консьюмер");
            consumer.close();
        }
    }

    @PreDestroy
    public void shutdown() {
        running = false;
        consumer.wakeup();
        log.info("Shutting down SnapshotProcessor...");
    }
}
