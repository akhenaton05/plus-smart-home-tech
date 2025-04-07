package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.yandex.practicum.analyzer.HubEventProcessor;
import ru.yandex.practicum.analyzer.SnapshotProcessor;

@SpringBootApplication(scanBasePackages = "ru.yandex.practicum")
public class AnalyzerApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(AnalyzerApplication.class, args);
        HubEventProcessor hubEventProcessor = context.getBean(HubEventProcessor.class);
        SnapshotProcessor snapshotProcessor = context.getBean(SnapshotProcessor.class);

        Thread hubThread = new Thread(hubEventProcessor);
        hubThread.setName("HubEventHandlerThread");
        hubThread.start();

        snapshotProcessor.start();
    }
}