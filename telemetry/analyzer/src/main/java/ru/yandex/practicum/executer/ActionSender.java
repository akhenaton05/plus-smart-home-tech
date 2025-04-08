package ru.yandex.practicum.executer;

import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.entity.Action;
import ru.yandex.practicum.entity.Scenario;
import ru.yandex.practicum.entity.ScenarioAction;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ActionSender {

    HubRouterControllerGrpc.HubRouterControllerStub hubRouterClient;

    public void send(Scenario scenario, SensorsSnapshotAvro snapshot) {
        log.info("gRPC client target address: {}", hubRouterClient.getChannel().authority());
        String hubId = snapshot.getHubId();
        List<ScenarioAction> actions = scenario.getActions();

        for (ScenarioAction scenarioAction : actions) {
            Action action = scenarioAction.getAction();

            DeviceActionProto actionProto = DeviceActionProto.newBuilder()
                    .setSensorId(scenarioAction.getSensor().getId())
                    .setType(ActionTypeProto.valueOf(action.getType().toString()))
                    .setValue(Objects.nonNull(action.getValue()) ? action.getValue() : null)
                    .build();

            Instant timestamp = snapshot.getTimestamp();

            DeviceActionRequest request = DeviceActionRequest.newBuilder()
                    .setHubId(hubId)
                    .setScenarioName(scenario.getName())
                    .setAction(actionProto)
                    .setTimestamp(Timestamp.newBuilder()
                            .setSeconds(timestamp.getEpochSecond())
                            .setNanos(timestamp.getNano())
                            .build())
                    .build();

            log.info("Sending async request - hubId: {}, scenarioName: {}, actionSensorId: {}, actionType: {}, actionValue: {}",
                    request.getHubId(), request.getScenarioName(),
                    request.getAction().getSensorId(), request.getAction().getType(), request.getAction().getValue());

            StreamObserver<Empty> responseObserver = new StreamObserver<>() {
                @Override
                public void onNext(Empty response) {
                    log.info("Successfully sent action for scenario {}: sensorId={}, type={}, value={}",
                            scenario.getName(), scenarioAction.getSensor().getId(), action.getType(),
                            action.getValue() != null ? action.getValue() : "none");
                }

                @Override
                public void onError(Throwable t) {
                    log.error("Failed to send action for scenario {}: {}", scenario.getName(), t.getMessage());
                }

                @Override
                public void onCompleted() {
                    log.info("gRPC request for scenario {} completed", scenario.getName());
                }
            };

            hubRouterClient.handleDeviceAction(request, responseObserver);
        }
    }
}
