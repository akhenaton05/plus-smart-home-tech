package ru.yandex.practicum.dto.util;

public class FeignClientException extends RuntimeException {
  private final int status;
  private final ErrorResponse errorResponse;

  public FeignClientException(int status, ErrorResponse errorResponse, String message) {
    super(message);
    this.status = status;
    this.errorResponse = errorResponse;
  }

  public int getStatus() {
    return status;
  }

  public ErrorResponse getErrorResponse() {
    return errorResponse;
  }
}
