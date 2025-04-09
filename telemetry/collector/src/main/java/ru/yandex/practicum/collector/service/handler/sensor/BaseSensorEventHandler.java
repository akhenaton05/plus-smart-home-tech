package ru.yandex.practicum.collector.service.handler.sensor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.collector.event.TopicType;
import ru.yandex.practicum.collector.service.KafkaEventProducer;
import ru.yandex.practicum.collector.service.handler.SensorEventHandler;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.time.Instant;


@Slf4j
@RequiredArgsConstructor
public abstract class BaseSensorEventHandler<T extends SpecificRecordBase> implements SensorEventHandler {
    protected final KafkaEventProducer producer;

    protected abstract T mapToAvro(SensorEventProto event);

    @Override
    public void handle(SensorEventProto event) {
        T payload = mapToAvro(event);

        Instant timestamp = Instant.ofEpochSecond(
                event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()
        );

        SensorEventAvro eventAvro = SensorEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setId(event.getId())
                .setTimestamp(timestamp)
                .setPayload(payload)
                .build();

        log.info("Sending SensorEventAvro to Kafka: id={}", event.getId());
        producer.send(TopicType.SENSOR_EVENTS, eventAvro);
    }
}