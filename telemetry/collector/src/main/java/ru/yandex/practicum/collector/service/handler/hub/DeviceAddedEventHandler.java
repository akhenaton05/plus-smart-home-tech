package ru.yandex.practicum.collector.service.handler.hub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.collector.event.hub.DeviceAddedEvent;
import ru.yandex.practicum.collector.event.hub.DeviceType;
import ru.yandex.practicum.collector.event.hub.HubEvent;
import ru.yandex.practicum.collector.event.hub.HubEventType;
import ru.yandex.practicum.collector.service.KafkaEventProducer;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;

@Service
@Slf4j
public class DeviceAddedEventHandler extends BaseHubEventHandler<DeviceAddedEventAvro> {
    public DeviceAddedEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    protected DeviceAddedEventAvro mapToAvro(HubEvent event) {
        DeviceAddedEvent _event = (DeviceAddedEvent) event;
        DeviceType eventType = _event.getDeviceType();
        DeviceTypeAvro avroType = DeviceTypeAvro.valueOf(eventType.toString());

        return DeviceAddedEventAvro.newBuilder()
                .setId(_event.getId())
                .setType(avroType)
                .build();
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.DEVICE_ADDED;
    }
}
