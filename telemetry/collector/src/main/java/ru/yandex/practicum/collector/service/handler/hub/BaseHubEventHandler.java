package ru.yandex.practicum.collector.service.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.collector.event.TopicType;
import ru.yandex.practicum.collector.service.KafkaEventProducer;
import ru.yandex.practicum.collector.service.handler.HubEventHandler;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseHubEventHandler<T extends SpecificRecordBase> implements HubEventHandler {
    protected final KafkaEventProducer producer;

    protected abstract T mapToAvro(HubEventProto event);

    @Override
    public void handle(HubEventProto event) {
        T payload = mapToAvro(event);

        Instant timestamp = Instant.ofEpochSecond(
                event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()
        );

        HubEventAvro eventAvro = HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(timestamp)
                .setPayload(payload)
                .build();

        log.info("Sending HubEventAvro to Kafka: id={}", event.getHubId());
        producer.send(TopicType.HUBS_EVENTS, eventAvro);
    }
}
