package ru.yandex.practicum.collector.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.collector.event.hub.HubEvent;
import ru.yandex.practicum.collector.event.hub.HubEventType;
import ru.yandex.practicum.collector.event.sensor.SensorEvent;
import ru.yandex.practicum.collector.event.sensor.SensorEventType;
import ru.yandex.practicum.collector.service.handler.HubEventHandler;
import ru.yandex.practicum.collector.service.handler.SensorEventHandler;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(path = "/events", consumes = MediaType.APPLICATION_JSON_VALUE)
public class CollectorController {
    private final Map<SensorEventType, SensorEventHandler> sensorEventHandlers;
    private final Map<HubEventType, HubEventHandler> hubEventHandlers;

    public CollectorController(List<SensorEventHandler> sensorEventHandlers, List<HubEventHandler> hubEventHandlers) {
        this.sensorEventHandlers = sensorEventHandlers.stream()
                .collect(Collectors.toMap(SensorEventHandler::getMessageType, Function.identity()));
        this.hubEventHandlers = hubEventHandlers.stream()
                .collect(Collectors.toMap(HubEventHandler::getMessageType, Function.identity()));
    }

    @PostMapping("/sensors")
    public ResponseEntity<Void> collectSensorEvent(@Valid @RequestBody SensorEvent event) {
        log.trace("\nEventController.postSensorEvent: accepted {}", event);
        if (sensorEventHandlers.containsKey(event.getType())) {
            sensorEventHandlers.get(event.getType()).handle(event);
            log.trace("\nEventController.postSensorEvent: returned {}");
            return ResponseEntity.ok().build();
        } else {
            throw new IllegalArgumentException("No handler for sensor event type: " + event.getType());
        }
    }

    @PostMapping("/hubs")
    public ResponseEntity<Void> collectHubEvent(@Valid @RequestBody HubEvent event) {
        log.trace("\nEventController.postHubsEvent: accepted {}", event);
        if (hubEventHandlers.containsKey(event.getType())) {
            hubEventHandlers.get(event.getType()).handle(event);
            return ResponseEntity.ok().build();
        } else {
            throw new IllegalArgumentException("No handler for hub event type: " + event.getType());
        }
    }
}