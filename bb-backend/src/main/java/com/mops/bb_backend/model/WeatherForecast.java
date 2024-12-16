package com.mops.bb_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "weather_forecasts")
@Builder
public class WeatherForecast {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "max_temperature", nullable = false)
    private double maxTemperature;

    @Column(name = "min_temperature", nullable = false)
    private double minTemperature;

    @Column(name = "weather", nullable = false)
    private String weather;

    @Column(name = "wind_speed", nullable = false)
    private double windSpeed;

    @Column(name = "extreme_condition", nullable = false)
    private String extremeCondition;

    @Column(name = "icon")
    private String icon;
}
