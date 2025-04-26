package ru.yandex.practicum.dto.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StackTraceElementDto {
    private String classLoaderName;
    private String moduleName;
    private String moduleVersion;
    private String methodName;
    private String fileName;
    private int lineNumber;
    private String className;
    private boolean nativeMethod;
}
