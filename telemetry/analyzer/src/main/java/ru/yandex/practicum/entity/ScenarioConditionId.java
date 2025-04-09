package ru.yandex.practicum.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScenarioConditionId {
    private Long scenarioId;
    private String sensorId;
    private Long conditionId;
}