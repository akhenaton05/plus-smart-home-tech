package ru.yandex.practicum.collector.service.handler.hub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.collector.event.hub.HubEvent;
import ru.yandex.practicum.collector.event.hub.HubEventType;
import ru.yandex.practicum.collector.event.hub.ScenarioAddedEvent;
import ru.yandex.practicum.collector.service.KafkaEventProducer;
import ru.yandex.practicum.collector.service.handler.hub.mapper.ActionMapper;
import ru.yandex.practicum.collector.service.handler.hub.mapper.ConditionsMapper;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;

@Service
@Slf4j
public class ScenarioAddedEventHandler extends BaseHubEventHandler<ScenarioAddedEventAvro> {
    public ScenarioAddedEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    protected ScenarioAddedEventAvro mapToAvro(HubEvent event) {
        ScenarioAddedEvent _event = (ScenarioAddedEvent) event;

        return ScenarioAddedEventAvro.newBuilder()
                .setName(_event.getName())
                .setActions(ActionMapper.actionListToAvro(_event.getActions()))
                .setConditions(ConditionsMapper.condListToAvro(_event.getConditions()))
                .build();
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.SCENARIO_ADDED;
    }
}
