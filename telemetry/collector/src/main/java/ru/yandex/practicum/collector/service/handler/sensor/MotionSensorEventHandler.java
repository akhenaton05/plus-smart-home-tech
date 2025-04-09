package ru.yandex.practicum.collector.service.handler.sensor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.collector.service.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;

@Service
@Slf4j
public class MotionSensorEventHandler extends BaseSensorEventHandler<MotionSensorAvro> {

    public MotionSensorEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    protected MotionSensorAvro mapToAvro(SensorEventProto event) {
        MotionSensorProto _event = event.getMotionSensorEvent();

        return MotionSensorAvro.newBuilder()
                .setMotion(_event.getMotion())
                .setLinkQuality(_event.getLinkQuality())
                .setVoltage(_event.getVoltage())
                .build();
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.MOTION_SENSOR_EVENT;
    }
}
