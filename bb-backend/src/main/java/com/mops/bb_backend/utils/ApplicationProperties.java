package com.mops.bb_backend.utils;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
@Getter
public class ApplicationProperties {
    @Value( "${openai.api.key}" )
    private String openApiKey;

    @Value( "${sendgrid.api.key}" )
    private String sendgridApiKey;

    @Value( "${sendgrid.from.email}" )
    private String sendgridFromEmail;

    @Value( "${sendgrid.from.name}" )
    private String sendgridFromName;

    @Value("${openweather.api.key}")
    private String openWeatherApiKey;

    @Value("${openweather.api.weather.url}")
    private String openWeatherApiWeatherUrl;

    @Value("${openweather.api.forecast.url}")
    private String openWeatherApiForecastUrl;

    @Value("${openweather.api.icons.url}")
    private String openWeatherApiIconsUrl;

    @Value("${geolocation.api.key}")
    private String geolocationApiKey;
}
