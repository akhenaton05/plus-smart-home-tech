package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.entity.*;
import ru.yandex.practicum.executer.ActionSender;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.repository.ScenarioRepository;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class SnapshotServiceImpl {

    private final ScenarioRepository scenarioRepository;
    private final ActionSender actionSender;

    @Transactional(readOnly = true)
    public void processSnapshot(SensorsSnapshotAvro snapshot) {
        log.info("Processing snapshot for hubId: {}", snapshot.getHubId());
        log.info("Available sensors in snapshot: {}", snapshot.getSensorsState());
        List<Scenario> scenarioList = scenarioRepository.findByHubId(snapshot.getHubId());
        for (Scenario scenario : scenarioList) {
            System.out.println("Scenario: " + scenario.getName());
            System.out.println("Conditions size: " + (scenario.getConditions() != null ? scenario.getConditions().size() : "null"));
            System.out.println("Actions size: " + (scenario.getActions() != null ? scenario.getActions().size() : "null"));
        }
        System.out.println("SCENARIO LIST " + scenarioList);
        for (Scenario scenario : scenarioList) {
            System.out.println("SCENARIO HUBID " + scenario.getHubId());
            if (checkConditions(scenario, snapshot)) {
                System.out.println("SUCCESS " + scenario + " ||||| " + snapshot);
                sendAction(scenario, snapshot);
            }
        }
    }

    public boolean checkConditions(Scenario scenario, SensorsSnapshotAvro snapshot) {
        List<ScenarioCondition> conditionList = scenario.getConditions();
        Map<String, SensorStateAvro> sensorsState = snapshot.getSensorsState();
        for (ScenarioCondition scenarioCondition : conditionList) {
            String sensorId = scenarioCondition.getSensor().getId();
            SensorStateAvro sensorState = sensorsState.get(sensorId);
            Condition condition = scenarioCondition.getCondition();

            if (Objects.isNull(sensorState)) {
                log.warn("Sensor {} not found in snapshot for scenario {}", sensorId, scenario.getName());
                return false;
            }

            if (!getValue(sensorState.getData(), condition)) {
                log.info("Condition not met for sensor {} in scenario {}: condition={}",
                        sensorId, scenario.getName(), condition);
                return false;
            }
        }
        log.info("All conditions met for scenario: {}", scenario.getName());
        return true;
    }

    private boolean getValue(Object data, Condition condition) {
        try {
            return switch (condition.getType()) {
                case TEMPERATURE ->  {
                    if (data instanceof TemperatureSensorAvro tempData) {
                        yield evaluateCondition(tempData.getTemperatureC(), condition.getOperation(), condition.getValue());
                    } else if (data instanceof ClimateSensorAvro climateData) {
                        yield evaluateCondition(climateData.getTemperatureC(), condition.getOperation(), condition.getValue());
                    }
                    log.error("Unsupported data type for TEMPERATURE condition: {}", data.getClass());
                    yield false;
                }
                case HUMIDITY -> data instanceof ClimateSensorAvro tempData &&
                        evaluateCondition(
                                tempData.getHumidity(),
                                condition.getOperation(),
                                condition.getValue()
                        );
                case CO2LEVEL -> data instanceof ClimateSensorAvro tempData &&
                        evaluateCondition(
                                tempData.getCo2Level(),
                                condition.getOperation(),
                                condition.getValue()
                        );
                case LUMINOSITY -> data instanceof LightSensorAvro tempData &&
                        evaluateCondition(
                                tempData.getLuminosity(),
                                condition.getOperation(),
                                condition.getValue()
                        );
                case MOTION -> data instanceof MotionSensorAvro tempData &&
                        evaluateCondition(
                                tempData.getMotion() ? 1 : 0,
                                condition.getOperation(),
                                condition.getValue()
                        );
                case SWITCH -> data instanceof SwitchSensorAvro tempData &&
                        evaluateCondition(
                                tempData.getState() ? 1 : 0,
                                condition.getOperation(),
                                condition.getValue()
                        );
            };
        } catch (ClassCastException e) {
            log.error("Type mismatch for condition type {}: {}", condition.getType(), e.getMessage());
            return false;
        }
    }

    private boolean evaluateCondition(int sensorValue, ConditionOperationType operation, Integer targetValue) {
        if (targetValue == null) {
            log.info("Target value is null for operation {}", operation);
            return false;
        }
        log.info("SENSOR VALUE: {}", sensorValue);
        log.info("OPERATION: {}", operation);
        log.info("TARGET VALUE: {}", targetValue);
        return switch (operation) {
            case EQUALS -> sensorValue == targetValue;
            case GREATER_THAN -> sensorValue > targetValue;
            case LOWER_THAN -> sensorValue < targetValue;
        };
    }

    private void sendAction(Scenario scenario, SensorsSnapshotAvro snapshot) {
        actionSender.send(scenario, snapshot);
        log.info("scenario send {} on hub {}", scenario.getId(), snapshot.getHubId());
    }
}
