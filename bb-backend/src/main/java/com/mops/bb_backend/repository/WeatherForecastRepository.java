package com.mops.bb_backend.repository;

import com.mops.bb_backend.model.User;
import com.mops.bb_backend.model.WeatherForecast;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface WeatherForecastRepository extends CrudRepository<WeatherForecast, UUID>{
    @Query("SELECT wf FROM WeatherForecast wf WHERE wf.date = :date AND wf.user = :user")
    Optional<WeatherForecast> findByDate(LocalDate date, User user);
}
