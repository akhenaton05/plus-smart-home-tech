package ru.yandex.practicum.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "scenario_conditions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioCondition {
    @EmbeddedId
    private ScenarioConditionId id;

    @ManyToOne
    @MapsId("scenarioId")
    @JoinColumn(name = "scenario_id")
    @ToString.Exclude
    private Scenario scenario;

    @ManyToOne
    @MapsId("sensorId")
    @JoinColumn(name = "sensor_id")
    @ToString.Exclude
    private Sensor sensor;

    @ManyToOne
    @MapsId("conditionId")
    @JoinColumn(name = "condition_id")
    @ToString.Exclude
    private Condition condition;
}
