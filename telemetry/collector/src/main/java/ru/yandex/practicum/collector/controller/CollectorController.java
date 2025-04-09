package ru.yandex.practicum.collector.controller;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.collector.service.handler.HubEventHandler;
import ru.yandex.practicum.collector.service.handler.SensorEventHandler;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@GrpcService
@Slf4j
public class CollectorController extends CollectorControllerGrpc.CollectorControllerImplBase {
    private final Map<SensorEventProto.PayloadCase, SensorEventHandler> sensorEventHandlers;
    private final Map<HubEventProto.PayloadCase, HubEventHandler> hubEventHandlers;

    public CollectorController(List<SensorEventHandler> sensorEventHandlers, List<HubEventHandler> hubEventHandlers) {
        this.sensorEventHandlers = sensorEventHandlers.stream()
                .collect(Collectors.toMap(SensorEventHandler::getMessageType, Function.identity()));
        this.hubEventHandlers = hubEventHandlers.stream()
                .collect(Collectors.toMap(HubEventHandler::getMessageType, Function.identity()));
    }

    @Override
    public void collectSensorEvent(SensorEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            log.info("Received sensor event: id={}, type={}", request.getId(), request.getPayloadCase());
            if (sensorEventHandlers.containsKey(request.getPayloadCase())) {
                sensorEventHandlers.get(request.getPayloadCase()).handle(request);
                responseObserver.onNext(Empty.getDefaultInstance());
                responseObserver.onCompleted();
            } else {
                log.error("No handler found for sensor event type: {}", request.getPayloadCase());
                throw new IllegalArgumentException("No handler for sensor event type: " + request.getPayloadCase());
            }
        } catch (Exception e) {
            log.error("Error processing sensor event: id={}, type={}, error={}", request.getId(), request.getPayloadCase(), e.getMessage(), e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }

    @Override
    public void collectHubEvent(HubEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            log.info("Received hub event: hubId={}, type={}", request.getHubId(), request.getPayloadCase());
            if (hubEventHandlers.containsKey(request.getPayloadCase())) {
                hubEventHandlers.get(request.getPayloadCase()).handle(request);
                responseObserver.onNext(Empty.getDefaultInstance());
                responseObserver.onCompleted();
            } else {
                log.error("No handler found for hub event type: {}", request.getPayloadCase());
                throw new IllegalArgumentException("No handler for hub event type: " + request.getPayloadCase());
            }
        } catch (Exception e) {
            log.error("Error processing hub event: hubId={}, type={}, error={}", request.getHubId(), request.getPayloadCase(), e.getMessage(), e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }

    }
}