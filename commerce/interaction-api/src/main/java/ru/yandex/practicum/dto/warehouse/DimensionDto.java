package ru.yandex.practicum.dto.warehouse;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DimensionDto {
    private double width;
    private double height;
    private double depth;
}
