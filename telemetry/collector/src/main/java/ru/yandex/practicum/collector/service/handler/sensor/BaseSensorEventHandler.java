package ru.yandex.practicum.collector.service.handler.sensor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.collector.event.TopicType;
import ru.yandex.practicum.collector.event.sensor.SensorEvent;
import ru.yandex.practicum.collector.service.KafkaEventProducer;
import ru.yandex.practicum.collector.service.handler.SensorEventHandler;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;


@Slf4j
@RequiredArgsConstructor
public abstract class BaseSensorEventHandler<T extends SpecificRecordBase> implements SensorEventHandler {
    protected final KafkaEventProducer producer;

    protected abstract T mapToAvro(SensorEvent event);

    @Override
    public void handle(SensorEvent event) {
        if (!event.getType().equals(getMessageType())) {
            throw new IllegalArgumentException("Неверный тип события: " + event);
        }



        T payload = mapToAvro(event);

        SensorEventAvro eventAvro = SensorEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setId(event.getId())
                .setTimestamp(event.getTimestamp())
                .setPayload(payload)
                .build();

        log.info("Sending SensorEventAvro to Kafka: id={}", event.getId());
        producer.send(TopicType.SENSOR_EVENTS, eventAvro);
    }
}