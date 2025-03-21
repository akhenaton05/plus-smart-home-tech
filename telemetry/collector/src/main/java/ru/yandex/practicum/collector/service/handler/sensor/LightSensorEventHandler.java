package ru.yandex.practicum.collector.service.handler.sensor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.collector.event.sensor.LightSensorEvent;
import ru.yandex.practicum.collector.event.sensor.SensorEvent;
import ru.yandex.practicum.collector.event.sensor.SensorEventType;
import ru.yandex.practicum.collector.service.KafkaEventProducer;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;

@Service
@Slf4j
public class LightSensorEventHandler extends BaseSensorEventHandler<LightSensorAvro>{

    public LightSensorEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    protected LightSensorAvro mapToAvro(SensorEvent event) {
        LightSensorEvent _event = (LightSensorEvent) event;

        return LightSensorAvro.newBuilder()
                .setLuminosity(_event.getLuminosity())
                .setLinkQuality(_event.getLinkQuality())
                .build();

    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.LIGHT_SENSOR_EVENT;
    }
}
