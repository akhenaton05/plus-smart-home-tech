package ru.yandex.practicum.dto.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ThrowableDto {
    private String message;
    private String localizedMessage;
    private List<StackTraceElementDto> stackTrace;
}
