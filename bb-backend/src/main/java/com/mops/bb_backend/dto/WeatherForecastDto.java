package com.mops.bb_backend.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record WeatherForecastDto(
        LocalDate date,
        double maxTemperature,
        double minTemperature,
        String weather,
        double windSpeed,
        String extremeCondition,
        String icon
) {
}
