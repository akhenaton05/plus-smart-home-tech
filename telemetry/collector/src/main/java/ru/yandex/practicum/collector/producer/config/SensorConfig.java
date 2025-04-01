package ru.yandex.practicum.collector.producer.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "sensor")
@Getter
@Setter
public class SensorConfig {
    private List<MotionSensor> motionSensors;
    private List<SwitchSensor> switchSensors;
    private List<TemperatureSensor> temperatureSensors;
    private List<LightSensor> lightSensors;
    private List<ClimateSensor> climateSensors;

    @Getter
    @Setter
    public static class MotionSensor {
        private String id;
        private Range linkQuality;
        private Range voltage;
        private boolean motion;
    }

    @Getter
    @Setter
    public static class SwitchSensor {
        private String id;
        private boolean state;
    }

    @Getter
    @Setter
    public static class TemperatureSensor {
        private String id;
        private Range temperature;
        private int currentTemperature;
    }

    @Getter
    @Setter
    public static class LightSensor {
        private String id;
        private Range luminosity;
        private int currentLuminosity;
    }

    @Getter
    @Setter
    public static class ClimateSensor {
        private String id;
        private Range temperature;
        private Range humidity;
        private Range co2Level;
        private int currentTemperature;
        private int currentHumidity;
        private int currentCo2Level;
    }

    @Getter
    @Setter
    public static class Range {
        private int minValue;
        private int maxValue;
    }
}