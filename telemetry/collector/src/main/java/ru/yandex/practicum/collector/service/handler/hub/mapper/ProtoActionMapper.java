package ru.yandex.practicum.collector.service.handler.hub.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.event.hub.ActionType;
import ru.yandex.practicum.collector.event.hub.DeviceAction;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;

import java.util.List;

@Component
public class ProtoActionMapper {

    public static DeviceAction actionProtoToDeviceAction(DeviceActionProto deviceProto) {
        ActionTypeProto actionType = deviceProto.getType();
        ActionType thisType = ActionType.valueOf(actionType.name());
        return DeviceAction.builder()
                .sensorId(deviceProto.getSensorId())
                .type(thisType)
                .value(deviceProto.getValue())
                .build();
    }

    public static List<DeviceAction> actionProtoListToActionList(List<DeviceActionProto> devProtoList) {
        return devProtoList.stream()
                .map(ProtoActionMapper::actionProtoToDeviceAction)
                .toList();
    }
}
