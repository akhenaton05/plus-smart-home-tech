package ru.yandex.practicum.collector.service.handler.hub.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.event.hub.ScenarioCondition;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
public class ConditionsMapper {
    public static ScenarioConditionAvro eventToAvro(ScenarioCondition scenario) {
        if (Objects.isNull(scenario)) {
            throw new IllegalArgumentException("ScenarioCondition cannot be null");
        }

        ScenarioConditionAvro.Builder builder = ScenarioConditionAvro.newBuilder();
        builder.setSensorId(scenario.getSensorId());

        try {
            ConditionTypeAvro avroType = ConditionTypeAvro.valueOf(scenario.getType().name());
            builder.setType(avroType);

            ConditionOperationAvro avroOperation = ConditionOperationAvro.valueOf(scenario.getOperation().name());
            builder.setOperation(avroOperation);

            Object value = scenario.getValue();
            switch (value) {
                case Integer i -> builder.setValue(i);
                case Boolean b -> builder.setValue(b);
                case null -> builder.setValue(null);
                default -> throw new IllegalArgumentException("Unsupported value type: " + value.getClass());
            }

            return builder.build();

        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Error mapping ScenarioCondition to Avro: " + e.getMessage(), e);
        }
    }

    public static List<ScenarioConditionAvro> condListToAvro(List<ScenarioCondition> conditions) {
        if (Objects.isNull(conditions) || conditions.isEmpty()) {
            return Collections.emptyList();
        }

        return conditions.stream()
                .map(ConditionsMapper::eventToAvro)
                .toList();
    }
}
