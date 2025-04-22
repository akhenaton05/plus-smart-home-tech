package ru.yandex.practicum.dto.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse {
  private HttpStatus httpStatus;
  private String userMessage;
  private String message;
  private String localizedMessage;
  private List<StackTraceElementDto> stackTrace;
  private ThrowableDto cause;
  private List<ThrowableDto> suppressed;
  private Object details;
}

