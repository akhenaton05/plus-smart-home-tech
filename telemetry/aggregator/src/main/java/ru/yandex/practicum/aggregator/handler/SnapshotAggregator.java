package ru.yandex.practicum.aggregator.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
public class SnapshotAggregator {
    private final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();

    Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        if (event == null) {
            return Optional.empty();
        }

        String hubId = event.getHubId();
        String sensorId = event.getId();

        SensorsSnapshotAvro snapshot = snapshots.computeIfAbsent(hubId, k -> {
            return SensorsSnapshotAvro.newBuilder()
                    .setHubId(hubId)
                    .setTimestamp(event.getTimestamp())
                    .setSensorsState(new HashMap<>())
                    .build();
        });

        Map<String, SensorStateAvro> sensorsState = snapshot.getSensorsState();
        SensorStateAvro oldState = sensorsState.get(sensorId);

        if (Objects.nonNull(oldState)) {
            if (oldState.getTimestamp().isAfter(event.getTimestamp()) || oldState.getData().equals(event.getPayload())) {
                return Optional.empty();
            }
        }

        SensorStateAvro newState = SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(event.getPayload())
                .build();

        sensorsState.put(sensorId, newState);
        snapshot.setSensorsState(sensorsState);
        snapshot.setTimestamp(event.getTimestamp());

        log.info("Снапшот для хаба {} обновлен: датчик {}", hubId, sensorId);
        return Optional.of(snapshot);
    }
}
