package com.mops.bb_backend.repository;

import com.mops.bb_backend.model.WeatherForecast;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface WeatherForecastRepository extends CrudRepository<WeatherForecast, UUID>{
    Optional<WeatherForecast> findByDate(LocalDate date);
}
