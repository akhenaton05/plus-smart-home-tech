package ru.yandex.practicum.analyzer;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.config.KafkaConfig;
import ru.yandex.practicum.entity.Sensor;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.repository.SensorRepository;
import ru.yandex.practicum.service.ScenarioService;
import ru.yandex.practicum.service.SensorService;

import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {

    private final KafkaConfig kafkaConfig;
    private final KafkaConsumer<String, HubEventAvro> consumer;
    private final SensorService sensorService;
    private final ScenarioService scenarioService;
    private volatile boolean running = true;

    @PostConstruct
    public void init() {
        Thread thread = new Thread(this, "HubEventHandlerThread");
        thread.start();
    }

    @Override
    public void run() {
        try {
            consumer.subscribe(Collections.singletonList(kafkaConfig.getHubConsumer().getTopic()));
            log.info("Subscribed to topic: {}", kafkaConfig.getHubConsumer().getTopic());

            while (running) {
                ConsumerRecords<String, HubEventAvro> records = consumer.poll(100);
                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    HubEventAvro event = record.value();
                    log.debug("Received event: {}", event);

                    processHubEvent(event);
                }
                consumer.commitSync();
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {
            log.info("Закрываем консьюмер");
            consumer.close();
        }
    }

    private void processHubEvent(HubEventAvro event) {
        String hubId = event.getHubId();
        Object payload = event.getPayload();

        if (payload instanceof DeviceAddedEventAvro) {
            sensorService.saveDevice((DeviceAddedEventAvro) payload, hubId);
        } else if (payload instanceof DeviceRemovedEventAvro) {
            sensorService.removeDevice((DeviceRemovedEventAvro) payload, hubId);
        } else if (payload instanceof ScenarioAddedEventAvro) {
            scenarioService.saveEvent((ScenarioAddedEventAvro) payload, hubId);
        } else if (payload instanceof ScenarioRemovedEventAvro) {
            scenarioService.removeEvent((ScenarioRemovedEventAvro) payload, hubId);
        }
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down AggregationStarter...");
        running = false;
        consumer.wakeup();
    }
}
