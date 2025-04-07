package ru.yandex.practicum.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.entity.*;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.repository.ActionRepository;
import ru.yandex.practicum.repository.ConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.repository.SensorRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioService {
    private final ScenarioRepository scenarioRepository;
    private final SensorRepository sensorRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;

    public void saveEvent(ScenarioAddedEventAvro event, String hubId) {
        Scenario scenario = Scenario.builder()
                .hubId(hubId)
                .name(event.getName())
                .build();

        scenarioRepository.save(scenario);

        scenario.setConditions(conditionHandle(event.getConditions(), hubId, scenario));
        scenario.setActions(actionHandle(event.getActions(), hubId, scenario));

        scenarioRepository.save(scenario);
        log.info("Added scenario: name={}, hubId={}", event.getName(), hubId);
    }

    public void removeEvent(ScenarioRemovedEventAvro event, String hubId) {
        Scenario scenario = scenarioRepository.findByHubIdAndName(hubId, event.getName())
                .orElseThrow(() -> new EntityNotFoundException("Scenario was not found"));
        scenarioRepository.delete(scenario);
        log.info("Removed scenario: name={}, hubId={}", event.getName(), hubId);
    }

    private List<ScenarioCondition> conditionHandle(List<ScenarioConditionAvro> avroList, String hubId, Scenario scenario) {
        return avroList.stream()
                .map(c -> {
                    Sensor sensor = sensorRepository.findByIdAndHubId(c.getSensorId(), hubId)
                            .orElseThrow(() -> new IllegalStateException("Sensor not found: " + c.getSensorId()));

                    Integer conditionValue = convertValue(c.getValue());

                    if (conditionValue == null) {
                        log.warn("Condition value is null for sensorId: {}, type: {}, operation: {}",
                                c.getSensorId(), c.getType(), c.getOperation());
                        return null; // Пропускаем условие, если значение не удалось преобразовать
                    }

                    Condition condition = Condition.builder()
                            .type(ConditionType.valueOf(c.getType().name()))
                            .operation(ConditionOperationType.valueOf(c.getOperation().name()))
                            .value(conditionValue)
                            .build();
                    conditionRepository.save(condition);

                    ScenarioConditionId id = ScenarioConditionId.builder()
                            .conditionId(condition.getId())
                            .scenarioId(scenario.getId())
                            .sensorId(sensor.getId())
                            .build();

                    return ScenarioCondition.builder()
                            .id(id)
                            .scenario(scenario)
                            .sensor(sensor)
                            .condition(condition)
                            .build();
                })
                .toList();
    }

    private List<ScenarioAction> actionHandle(List<DeviceActionAvro> avroList, String hubId, Scenario scenario) {
        return avroList.stream()
                .map(a -> {
                    Sensor sensor = sensorRepository.findByIdAndHubId(a.getSensorId(), hubId)
                            .orElseThrow(() -> new IllegalStateException("Sensor not found: " + a.getSensorId()));

                    Integer actionValue = convertValue(a.getValue());

                    if (actionValue == null) {
                        log.warn("Action value is null for sensorId: {}, type: {}",
                                a.getSensorId(), a.getType());
                        return null; // Пропускаем действие, если значение не удалось преобразовать
                    }

                    Action action = Action.builder()
                            .type(DeviceActionType.valueOf(a.getType().name()))
                            .value(actionValue)
                            .build();
                    actionRepository.save(action);

                    ScenarioActionId id = ScenarioActionId.builder()
                            .actionId(action.getId())
                            .scenarioId(scenario.getId())
                            .sensorId(sensor.getId())
                            .build();

                    return ScenarioAction.builder()
                            .id(id)
                            .action(action)
                            .scenario(scenario)
                            .sensor(sensor)
                            .build();
                })
                .toList();
    }

    private Integer convertValue(Object value) {
        if (value instanceof Integer integer) {
            return integer;
        } else if (value instanceof Boolean bool) {
            return bool ? 1 : 0;
        } else {
            log.warn("Unsupported value type: {}", value != null ? value.getClass() : null);
            return null;
        }
    }
}
