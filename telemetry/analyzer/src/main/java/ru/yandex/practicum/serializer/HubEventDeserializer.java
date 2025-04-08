package ru.yandex.practicum.serializer;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

public class HubEventDeserializer extends AvroDeserializer<HubEventAvro> {
    public HubEventDeserializer() {
        super(HubEventAvro.getClassSchema());
    }
}
