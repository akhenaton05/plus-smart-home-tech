package ru.yandex.practicum.collector.event.hub;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class DeviceAction {
    private String sensorId;
    private ActionType type;
    private Integer value;
}