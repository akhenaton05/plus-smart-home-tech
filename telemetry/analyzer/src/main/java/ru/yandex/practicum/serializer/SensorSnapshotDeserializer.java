package ru.yandex.practicum.serializer;

import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

public class SensorSnapshotDeserializer extends AvroDeserializer<SensorsSnapshotAvro> {
    public SensorSnapshotDeserializer() {
        super(SensorsSnapshotAvro.getClassSchema());
    }
}
