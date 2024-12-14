package com.mops.bb_backend.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ForecastApiResponseDto {

    private List<WeatherData> list;

    @Data
    public static class WeatherData {
        private Main main;
        private List<Weather> weather;
        private Wind wind;
        private String dt_txt;
    }

    @Data
    public static class Weather {
        private String main;
        private String description;
        private String icon;
    }

    @Data
    public static class Main {
        @JsonProperty("temp_min")
        private double tempMin;
        @JsonProperty("temp_max")
        private double tempMax;
    }

    @Data
    public static class Wind {
        private double speed;
    }
}
