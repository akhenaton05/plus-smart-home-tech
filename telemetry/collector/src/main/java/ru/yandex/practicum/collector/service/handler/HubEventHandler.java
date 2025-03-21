package ru.yandex.practicum.collector.service.handler;

import ru.yandex.practicum.collector.event.hub.HubEvent;
import ru.yandex.practicum.collector.event.hub.HubEventType;

public interface HubEventHandler {
    HubEventType getMessageType();
    void handle(HubEvent event);
}
