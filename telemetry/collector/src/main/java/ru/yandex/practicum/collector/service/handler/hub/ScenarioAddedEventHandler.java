package ru.yandex.practicum.collector.service.handler.hub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.collector.event.hub.*;
import ru.yandex.practicum.collector.service.KafkaEventProducer;
import ru.yandex.practicum.collector.service.handler.hub.mapper.ActionMapper;
import ru.yandex.practicum.collector.service.handler.hub.mapper.ConditionsMapper;
import ru.yandex.practicum.collector.service.handler.hub.mapper.ProtoActionMapper;
import ru.yandex.practicum.collector.service.handler.hub.mapper.ProtoConditionMapper;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;

import java.util.List;

@Service
@Slf4j
public class ScenarioAddedEventHandler extends BaseHubEventHandler<ScenarioAddedEventAvro> {

    public ScenarioAddedEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    protected ScenarioAddedEventAvro mapToAvro(HubEventProto event) {
        ScenarioAddedEventProto _event = event.getScenarioAdded();

        List<ScenarioCondition> conditionList =
                ProtoConditionMapper.condProtoListToCondList(_event.getConditionList());
        List<DeviceAction> actionList =
                ProtoActionMapper.actionProtoListToActionList(_event.getActionList());

        ScenarioAddedEvent addedEvent = ScenarioAddedEvent.builder()
                .name(_event.getName())
                .build();
        ScenarioAddedEventAvro.Builder builder = ScenarioAddedEventAvro.newBuilder();
        builder.setName(_event.getName());
        builder.setActions(ActionMapper.actionListToAvro(actionList));
        builder.setConditions(ConditionsMapper.condListToAvro(conditionList));
        return builder.build();
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }
}
