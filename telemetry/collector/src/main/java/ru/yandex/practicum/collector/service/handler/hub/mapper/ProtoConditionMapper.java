package ru.yandex.practicum.collector.service.handler.hub.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.event.hub.ConditionOperation;
import ru.yandex.practicum.collector.event.hub.ConditionType;
import ru.yandex.practicum.collector.event.hub.ScenarioCondition;
import ru.yandex.practicum.grpc.telemetry.event.ConditionOperationProto;
import ru.yandex.practicum.grpc.telemetry.event.ConditionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;

import java.util.List;

@Component
public class ProtoConditionMapper {

    public static ScenarioCondition conditionProtoToCondition(ScenarioConditionProto conditionProto) {
        ConditionTypeProto actType = conditionProto.getType();
        ConditionType thisType = ConditionType.valueOf(actType.name());

        ConditionOperationProto conditionOperationType = conditionProto.getOperation();
        ConditionOperation thisCond = ConditionOperation.valueOf(conditionOperationType.name());

        Object thisValue = switch (conditionProto.getValueCase()) {
            case BOOL_VALUE -> conditionProto.getBoolValue();
            case INT_VALUE -> conditionProto.getIntValue();
            case VALUE_NOT_SET -> null;
        };
        return ScenarioCondition.builder()
                .sensorId(conditionProto.getSensorId())
                .operation(thisCond)
                .type(thisType)
                .value(thisValue)
                .build();
    }

    public static List<ScenarioCondition> condProtoListToCondList(List<ScenarioConditionProto> condProtoList) {
        return condProtoList.stream()
                .map(ProtoConditionMapper::conditionProtoToCondition)
                .toList();
    }
}
