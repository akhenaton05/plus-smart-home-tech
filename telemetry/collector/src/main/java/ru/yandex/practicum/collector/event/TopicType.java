package ru.yandex.practicum.collector.event;

public enum TopicType {
    SENSOR_EVENTS,
    HUBS_EVENTS;

    public static TopicType from(String value) {
        return switch (value.toLowerCase()) {
            case "sensors-events" -> SENSOR_EVENTS;
            case "hubs-events" -> HUBS_EVENTS;
            default -> throw new IllegalArgumentException("Unknown topic type: " + value);
        };
    }
}