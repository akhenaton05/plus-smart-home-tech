package ru.yandex.practicum.collector.service.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.collector.event.TopicType;
import ru.yandex.practicum.collector.event.hub.HubEvent;
import ru.yandex.practicum.collector.service.KafkaEventProducer;
import ru.yandex.practicum.collector.service.handler.HubEventHandler;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseHubEventHandler<T extends SpecificRecordBase> implements HubEventHandler {
    protected final KafkaEventProducer producer;

    protected abstract T mapToAvro(HubEvent event);

    @Override
    public void handle(HubEvent event) {
        if (!event.getType().equals(getMessageType())) {
            throw new IllegalArgumentException("Неверный тип события: " + event);
        }

        T payload = mapToAvro(event);

        HubEventAvro eventAvro = HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(payload)
                .build();

        log.info("Sending HubEventAvro to Kafka: id={}", event.getHubId());
        producer.send(TopicType.HUBS_EVENTS, eventAvro);
    }
}
