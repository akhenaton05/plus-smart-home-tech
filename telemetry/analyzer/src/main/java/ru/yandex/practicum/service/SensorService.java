package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.entity.Sensor;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.repository.SensorRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class SensorService {
    private final SensorRepository sensorRepository;

    public void saveDevice(DeviceAddedEventAvro event, String hubId) {
        Sensor sensor = Sensor.builder()
            .id(event.getId())
            .hubId(hubId)
            .build();
        sensorRepository.save(sensor);
        log.info("Added sensor: id={}, hubId={}", event.getId(), hubId);
    }

    public void removeDevice(DeviceRemovedEventAvro event, String hubId) {
        sensorRepository.findByIdAndHubId(event.getId(), hubId).ifPresent(sensorRepository::delete);
        log.info("Removed sensor: id={}", event.getId());
    }
}
