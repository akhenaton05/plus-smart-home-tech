package ru.yandex.practicum.collector.service.handler.sensor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.collector.service.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;

@Service
@Slf4j
public class LightSensorEventHandler extends BaseSensorEventHandler<LightSensorAvro>{

    public LightSensorEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    protected LightSensorAvro mapToAvro(SensorEventProto event) {
        LightSensorProto _event = event.getLightSensorEvent();

        return LightSensorAvro.newBuilder()
                .setLuminosity(_event.getLuminosity())
                .setLinkQuality(_event.getLinkQuality())
                .build();
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR_EVENT;
    }
}
