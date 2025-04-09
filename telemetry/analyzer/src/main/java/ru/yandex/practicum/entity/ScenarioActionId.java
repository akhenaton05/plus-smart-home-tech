package ru.yandex.practicum.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScenarioActionId {
    private Long scenarioId;
    private String sensorId;
    private Long actionId;
}
