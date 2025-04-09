package ru.yandex.practicum.collector.producer;

import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.producer.config.SensorConfig;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.*;

import java.time.Instant;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventDataProducer {
    private static final Random random = new Random();
    private final SensorConfig sensorConfig;

//    @GrpcClient("collector")
//    private CollectorControllerGrpc.CollectorControllerBlockingStub collectorStub;

    @Scheduled(fixedRate = 10000)
    public void emulateEvents() {
        sensorConfig.getTemperatureSensors().forEach(this::sendTemperatureSensorEvent);
        sensorConfig.getMotionSensors().forEach(this::sendMotionSensorEvent);
        sensorConfig.getSwitchSensors().forEach(this::sendSwitchSensorEvent);
        sensorConfig.getLightSensors().forEach(this::sendLightSensorEvent);
        sensorConfig.getClimateSensors().forEach(this::sendClimateSensorEvent);
    }

    private void sendTemperatureSensorEvent(SensorConfig.TemperatureSensor sensor) {
        SensorEventProto event = createTemperatureSensorEvent(sensor);
        sendSensorEvent(event);
    }

    private SensorEventProto createTemperatureSensorEvent(SensorConfig.TemperatureSensor sensor) {
        int temperatureCelsius = getRandomSensorValue(sensor.getTemperature(), sensor.getCurrentTemperature());
        sensor.setCurrentTemperature(temperatureCelsius);
        int temperatureFahrenheit = (int) (temperatureCelsius * 1.8 + 32);
        Instant ts = Instant.now();

        return SensorEventProto.newBuilder()
                .setId(sensor.getId())
                .setHubId("hub-1")
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(ts.getEpochSecond())
                        .setNanos(ts.getNano())
                        .build())
                .setTemperatureSensorEvent(
                        TemperatureSensorProto.newBuilder()
                                .setTemperatureC(temperatureCelsius)
                                .setTemperatureF(temperatureFahrenheit)
                                .build()
                )
                .build();
    }

    private void sendMotionSensorEvent(SensorConfig.MotionSensor sensor) {
        SensorEventProto event = createMotionSensorEvent(sensor);
        sendSensorEvent(event);
    }

    private SensorEventProto createMotionSensorEvent(SensorConfig.MotionSensor sensor) {
        int linkQuality = getRandomSensorValue(sensor.getLinkQuality(), 0);
        int voltage = getRandomSensorValue(sensor.getVoltage(), 0);
        boolean motion = random.nextBoolean();
        sensor.setMotion(motion);

        Instant ts = Instant.now();
        return SensorEventProto.newBuilder()
                .setId(sensor.getId())
                .setHubId("hub-1")
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(ts.getEpochSecond())
                        .setNanos(ts.getNano())
                        .build())
                .setMotionSensorEvent(
                        MotionSensorProto.newBuilder()
                                .setLinkQuality(linkQuality)
                                .setMotion(motion)
                                .setVoltage(voltage)
                                .build()
                )
                .build();
    }

    private void sendSwitchSensorEvent(SensorConfig.SwitchSensor sensor) {
        SensorEventProto event = createSwitchSensorEvent(sensor);
        sendSensorEvent(event);
    }

    private SensorEventProto createSwitchSensorEvent(SensorConfig.SwitchSensor sensor) {
        boolean state = !sensor.isState();
        sensor.setState(state);

        Instant ts = Instant.now();
        return SensorEventProto.newBuilder()
                .setId(sensor.getId())
                .setHubId("hub-1")
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(ts.getEpochSecond())
                        .setNanos(ts.getNano())
                        .build())
                .setSwitchSensorEvent(
                        SwitchSensorProto.newBuilder()
                                .setState(state)
                                .build()
                )
                .build();
    }

    private void sendLightSensorEvent(SensorConfig.LightSensor sensor) {
        SensorEventProto event = createLightSensorEvent(sensor);
        sendSensorEvent(event);
    }

    private SensorEventProto createLightSensorEvent(SensorConfig.LightSensor sensor) {
        int luminosity = getRandomSensorValue(sensor.getLuminosity(), sensor.getCurrentLuminosity());
        sensor.setCurrentLuminosity(luminosity);
        int linkQuality = random.nextInt(100);

        Instant ts = Instant.now();
        return SensorEventProto.newBuilder()
                .setId(sensor.getId())
                .setHubId("hub-1")
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(ts.getEpochSecond())
                        .setNanos(ts.getNano())
                        .build())
                .setLightSensorEvent(
                        LightSensorProto.newBuilder()
                                .setLuminosity(luminosity)
                                .setLinkQuality(linkQuality)
                                .build()
                )
                .build();
    }

    private void sendClimateSensorEvent(SensorConfig.ClimateSensor sensor) {
        SensorEventProto event = createClimateSensorEvent(sensor);
        sendSensorEvent(event);
    }

    private SensorEventProto createClimateSensorEvent(SensorConfig.ClimateSensor sensor) {
        int temperatureCelsius = getRandomSensorValue(sensor.getTemperature(), sensor.getCurrentTemperature());
        int humidity = getRandomSensorValue(sensor.getHumidity(), sensor.getCurrentHumidity());
        int co2Level = getRandomSensorValue(sensor.getCo2Level(), sensor.getCurrentCo2Level());
        sensor.setCurrentTemperature(temperatureCelsius);
        sensor.setCurrentHumidity(humidity);
        sensor.setCurrentCo2Level(co2Level);

        Instant ts = Instant.now();
        return SensorEventProto.newBuilder()
                .setId(sensor.getId())
                .setHubId("hub-1")
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(ts.getEpochSecond())
                        .setNanos(ts.getNano())
                        .build())
                .setClimateSensorEvent(
                        ClimateSensorProto.newBuilder()
                                .setTemperatureC(temperatureCelsius)
                                .setHumidity(humidity)
                                .setCo2Level(co2Level)
                                .build()
                )
                .build();
    }

    private int getRandomSensorValue(SensorConfig.Range range, int prevValue) {
        int min = range.getMinValue();
        int max = range.getMaxValue();
        if (prevValue == 0) {
            return random.nextInt(max - min + 1) + min;
        }
        int delta = random.nextInt(11) - 5;
        int newValue = prevValue + delta;
        return Math.max(min, Math.min(max, newValue));
    }

    private void sendSensorEvent(SensorEventProto event) {
        log.info("Sending sensor event: id={}, type={}", event.getId(), event.getPayloadCase());
        try {
//            collectorStub.collectSensorEvent(event);
            log.info("Successfully sent sensor event: id={}", event.getId());
        } catch (Exception e) {
            log.error("Failed to send sensor event: id={}, error={}", event.getId(), e.getMessage(), e);
        }
    }
}
