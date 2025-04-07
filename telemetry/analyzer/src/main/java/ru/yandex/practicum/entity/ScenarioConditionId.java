package ru.yandex.practicum.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScenarioConditionId {
    Long scenarioId;
    String sensorId;
    Long conditionId;
}