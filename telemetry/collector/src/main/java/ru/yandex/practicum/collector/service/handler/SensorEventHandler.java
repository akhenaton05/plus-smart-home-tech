package ru.yandex.practicum.collector.service.handler;

import ru.yandex.practicum.collector.event.sensor.SensorEvent;
import ru.yandex.practicum.collector.event.sensor.SensorEventType;

public interface SensorEventHandler {
    SensorEventType getMessageType();
    void handle(SensorEvent event);
}
