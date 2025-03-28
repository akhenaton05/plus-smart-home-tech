package ru.yandex.practicum.collector.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@ToString
@ConfigurationProperties("topics")
@Validated
public class TopicConfig {
    @NotBlank
    @Pattern(regexp = "[a-z]+\\.[a-z]+\\.v[0-9]+", message = "Topic name must be in format <domain>.<type>.v<version>")
    private String sensorsEvents;

    @NotBlank
    @Pattern(regexp = "[a-z]+\\.[a-z]+\\.v[0-9]+", message = "Topic name must be in format <domain>.<type>.v<version>")
    private String hubsEvents;
}