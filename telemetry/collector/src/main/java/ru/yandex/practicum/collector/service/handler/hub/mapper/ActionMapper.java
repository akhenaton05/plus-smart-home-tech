package ru.yandex.practicum.collector.service.handler.hub.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.event.hub.ActionType;
import ru.yandex.practicum.collector.event.hub.DeviceAction;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;

import java.util.List;

@Component
public class ActionMapper {

    public static DeviceActionAvro eventToAvro(DeviceAction devAct) {
        ActionType thisType = devAct.getType();
        ActionTypeAvro actionTypeAvro = ActionTypeAvro.valueOf(thisType.name());
        return DeviceActionAvro.newBuilder()
                .setSensorId(devAct.getSensorId())
                .setType(actionTypeAvro)
                .setValue(devAct.getValue())
                .build();
    }

    public static List<DeviceActionAvro> actionListToAvro(List<DeviceAction> actions) {
        return actions.stream()
                .map(ActionMapper::eventToAvro)
                .toList();
    }
}
