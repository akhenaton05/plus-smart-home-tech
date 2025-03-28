package ru.yandex.practicum.collector.service.handler.hub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.collector.event.hub.HubEvent;
import ru.yandex.practicum.collector.event.hub.HubEventType;
import ru.yandex.practicum.collector.event.hub.ScenarioRemovedEvent;
import ru.yandex.practicum.collector.service.KafkaEventProducer;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

@Service
@Slf4j
public class ScenarioRemovedEventHandler extends BaseHubEventHandler<ScenarioRemovedEventAvro> {
    public ScenarioRemovedEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    protected ScenarioRemovedEventAvro mapToAvro(HubEvent event) {
        ScenarioRemovedEvent _event = (ScenarioRemovedEvent) event;

        return ScenarioRemovedEventAvro.newBuilder()
                .setName(_event.getName())
                .build();
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.SCENARIO_REMOVED;
    }
}
