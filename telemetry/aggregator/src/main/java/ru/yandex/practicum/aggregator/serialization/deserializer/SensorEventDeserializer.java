package ru.yandex.practicum.aggregator.serialization.deserializer;

import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

public class SensorEventDeserializer extends AvroDeserializer<SensorEventAvro> {

    public SensorEventDeserializer() {
        super(SensorEventAvro.getClassSchema());
    }
}
