package com.mops.bb_backend.controller;

import com.mops.bb_backend.dto.WeatherForecastDto;
import com.mops.bb_backend.model.WeatherForecast;
import com.mops.bb_backend.service.WeatherForecastService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@RestController
public class WeatherForecastController {
    private final WeatherForecastService weatherForecastService;

    @GetMapping("/forecast/{date}")
    public ResponseEntity<WeatherForecastDto> getForecastByDate(@PathVariable String date) {
        var localDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
        return new ResponseEntity<>(weatherForecastService.getForecastByDate(localDate), HttpStatus.OK);
    }
}
