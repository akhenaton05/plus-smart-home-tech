package ru.yandex.practicum.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;

@Slf4j
@Configuration
public class GrpcConfig {

    @Value("${grpc.client.hub-router.address}")
    private String address;

    @Bean
    public ManagedChannel grpcChannel() {
        log.info("Creating gRPC channel with address: {}", address);
        return ManagedChannelBuilder.forTarget(address)
                .usePlaintext()
                .build();
    }

    @Bean
    public HubRouterControllerGrpc.HubRouterControllerStub hubRouterStub(ManagedChannel channel) {
        log.info("Creating asynchronous gRPC stub for HubRouterController");
        return HubRouterControllerGrpc.newStub(channel);
    }
}
