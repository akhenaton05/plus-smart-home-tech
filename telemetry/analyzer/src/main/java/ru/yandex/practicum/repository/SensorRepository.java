package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.entity.Sensor;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long>{
    boolean existsByIdInAndHubId(Collection<String> ids, String hubId);
    Optional<Sensor> findByIdAndHubId(String id, String hubId);
}
