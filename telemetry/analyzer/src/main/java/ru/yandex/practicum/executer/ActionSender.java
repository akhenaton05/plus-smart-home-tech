package ru.yandex.practicum.executer;

import com.google.protobuf.Timestamp;
import io.grpc.StatusRuntimeException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.server.service.GrpcService;
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

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ActionSender {

    @GrpcClient("hub-router")
    HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    public void send(Scenario scenario, SensorsSnapshotAvro snapshot) {
        String hubId = snapshot.getHubId();
        List<ScenarioAction> actions = scenario.getActions();

        System.out.println("Scenario: " + scenario.getName());
        System.out.println("Actions (raw): " + scenario.getActions());

        for (ScenarioAction scenarioAction : actions) {
            System.out.println("scenarioAction: " + scenarioAction);
            System.out.println("sensor: " + scenarioAction.getSensor());
            System.out.println("action: " + scenarioAction.getAction());

            Action action = scenarioAction.getAction();
            System.out.println("ACTION " + action);

            DeviceActionProto actionProto = DeviceActionProto.newBuilder()
                    .setSensorId(scenarioAction.getSensor().getId())
                    .setType(ActionTypeProto.valueOf(action.getType().toString()))
                    .setValue(action.getValue() != null ? action.getValue() : null)
                    .build();

            log.info("actionProto - sensorId: {}, type: {}, value: {}",
                    actionProto.getSensorId(), actionProto.getType(), actionProto.getValue());
            //System.out.println("actionProto " + actionProto);

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
            log.info("Sending request - hubId: {}, scenarioName: {}, actionSensorId: {}, actionType: {}, actionValue: {}",
                    request.getHubId(), request.getScenarioName(),
                    request.getAction().getSensorId(), request.getAction().getType(), request.getAction().getValue());

            //System.out.println("request " + request);

            try {
                hubRouterClient.handleDeviceAction(request);
            } catch (StatusRuntimeException e) {
                log.error("Failed to send action to HubRouterController: {}", e.getMessage());
            }

            log.info("Sent action for scenario {}: sensorId={}, type={}, value={}",
                    scenario.getName(), scenarioAction.getSensor().getId(), action.getType(),
                    action.getValue() != null ? action.getValue() : "none");
        }
    }
}
