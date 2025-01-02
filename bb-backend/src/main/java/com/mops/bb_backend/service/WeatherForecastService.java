package com.mops.bb_backend.service;

import com.mops.bb_backend.dto.ForecastApiResponseDto;
import com.mops.bb_backend.dto.WeatherApiResponseDto;
import com.mops.bb_backend.dto.WeatherForecastDto;
import com.mops.bb_backend.exception.CustomException;
import com.mops.bb_backend.model.User;
import com.mops.bb_backend.model.WeatherForecast;
import com.mops.bb_backend.repository.WeatherForecastRepository;
import com.mops.bb_backend.utils.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WeatherForecastService {

    private final WeatherForecastRepository weatherForecastRepository;
    private final ApplicationProperties applicationProperties;
    private final UserService userService;

    public WeatherForecastDto getForecastByDate(LocalDate date) {
        var user = userService.getAuthenticatedUser();
        return getForecastByDateForUser(date, user);
    }

    private WeatherForecastDto getForecastByDateForUser(LocalDate date, User user) {

        return weatherForecastRepository.findByDate(date, user)
                .map(this::mapWeatherForecastToWeatherForecastDto)
                .orElseGet(() -> {
                    fetchAndSaveWeatherData(date, user);

                    return weatherForecastRepository.findByDate(date, user)
                            .map(this::mapWeatherForecastToWeatherForecastDto)
                            .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST,
                                    "No forecast found for the provided date!"));
                });
    }

    public WeatherForecastDto getTomorrowForecastForUser(User user) {
        return getForecastByDateForUser(LocalDate.now().plusDays(1), user);
    }

    public boolean isExtremeWeather(WeatherForecastDto forecast) {
        Set<String> extremeWeatherDescriptions = Set.of(
                // Extreme Thunderstorms
                "thunderstorm with heavy rain", "heavy thunderstorm", "ragged thunderstorm",
                "thunderstorm with heavy drizzle",
                // Extreme Rain
                "very heavy rain", "extreme rain", "freezing rain", "heavy intensity shower rain", "ragged shower rain",
                // Extreme Snow
                "heavy snow", "heavy shower snow",
                // Atmosphere
                "volcanic ash", "squalls", "tornado"
        );

        return extremeWeatherDescriptions.contains(forecast.extremeCondition().toLowerCase());
    }

    public void fetchAndSaveWeatherData(LocalDate date, User user) {
        var useWeatherUrl = date.isEqual(LocalDate.now());
        if (!useWeatherUrl && (!date.isAfter(LocalDate.now()) || !date.isBefore(LocalDate.now().plusDays(6))))
            return;

        RestTemplate restTemplate = new RestTemplate();

        String location = user.getLocation();
        if (location == null) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "User location is not set.");
        }
        String latitude = location.split(",")[0];
        String longitude = location.split(",")[1];

        String url = String.format("%s?lat=%s&lon=%s&appid=%s&units=metric",
                useWeatherUrl ? applicationProperties.getOpenWeatherApiWeatherUrl() : applicationProperties.getOpenWeatherApiForecastUrl(),
                latitude, longitude, applicationProperties.getOpenWeatherApiKey());

        var dtoClass = useWeatherUrl ? WeatherApiResponseDto.class : ForecastApiResponseDto.class;
        var response = restTemplate.getForObject(url, dtoClass);
        if (response == null)
            return;

        if (useWeatherUrl) {
            weatherForecastRepository.save(mapToWeatherForecast((WeatherApiResponseDto) response, user));
        } else {
            weatherForecastRepository.saveAll(mapToWeatherForecastsList((ForecastApiResponseDto) response, user));
        }
    }

    private WeatherForecastDto mapWeatherForecastToWeatherForecastDto(WeatherForecast forecast) {
        return WeatherForecastDto.builder()
                .date(forecast.getDate())
                .maxTemperature(forecast.getMaxTemperature())
                .minTemperature(forecast.getMinTemperature())
                .weather(forecast.getWeather())
                .windSpeed(forecast.getWindSpeed())
                .extremeCondition(forecast.getExtremeCondition())
                .icon(forecast.getIcon())
                .build();
    }

    private WeatherForecast mapToWeatherForecast(WeatherApiResponseDto responseDto, User user) {
            return WeatherForecast.builder()
                    .date(LocalDate.ofInstant(Instant.ofEpochSecond(responseDto.getDt()), ZoneId.of("UTC")))
                    .maxTemperature(responseDto.getMain().getTempMax())
                    .minTemperature(responseDto.getMain().getTempMin())
                    .windSpeed(responseDto.getWind().getSpeed())
                    .weather(responseDto.getWeather().getFirst().getMain())
                    .extremeCondition(responseDto.getWeather().getFirst().getDescription())
                    .icon(applicationProperties.getOpenWeatherApiIconsUrl() + responseDto.getWeather().getFirst().getIcon() + "@2x.png")
                    .user(user)
                    .build();
    }

    private List<WeatherForecast> mapToWeatherForecastsList(ForecastApiResponseDto responseDto, User user) {
        return responseDto.getList().stream()
                .filter(entry -> entry.getDt_txt().contains("12:00:00"))
                .map(entry -> WeatherForecast.builder()
                        .date(LocalDate.parse(entry.getDt_txt(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .maxTemperature(entry.getMain().getTempMax())
                        .minTemperature(entry.getMain().getTempMin())
                        .windSpeed(entry.getWind().getSpeed())
                        .weather(entry.getWeather().getFirst().getMain())
                        .extremeCondition(entry.getWeather().getFirst().getDescription())
                        .icon(applicationProperties.getOpenWeatherApiIconsUrl() + entry.getWeather().getFirst().getIcon() + "@2x.png")
                        .user(user)
                        .build()).collect(Collectors.toList());
    }

}
