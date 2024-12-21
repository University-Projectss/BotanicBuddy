import { apiClient } from "@/apiClient";
import { Flex, Image, Spinner, Text } from "@chakra-ui/react";
import { useQuery } from "@tanstack/react-query";
import clearIcon from "../../../assets/clear.svg";
import cloudsIcon from "../../../assets/clouds.svg";
import drizzleIcon from "../../../assets/drizzle.svg";
import otherIcon from "../../../assets/other.svg";
import rainIcon from "../../../assets/rain.svg";
import snowIcon from "../../../assets/snow.svg";
import thunderstormIcon from "../../../assets/thunderstorm.svg";
import { ForecastResponse, WeatherRecommendations } from "@/types/weather";
import dayjs from "dayjs";

const formatDate = (date: Date) => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0"); // Months are zero-based
  const day = String(date.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
};

const getWeatherIcon = (weather: string) => {
  switch (weather.toLowerCase()) {
    case "clear":
      return clearIcon;
    case "clouds":
      return cloudsIcon;
    case "drizzle":
      return drizzleIcon;
    case "rain":
      return rainIcon;
    case "snow":
      return snowIcon;
    case "thunderstorm":
      return thunderstormIcon;
    default:
      return otherIcon;
  }
};

const Weather = () => {
  const knownWeather = [
    "Clear",
    "Clouds",
    "Drizzle",
    "Rain",
    "Snow",
    "Thunderstorm",
  ];
  const { data: forecast, isLoading } = useQuery({
    queryKey: ["forecast"],
    retry: false,
    queryFn: async () => {
      const { data } = await apiClient.get<ForecastResponse>(
        `/forecast/${formatDate(new Date())}`
      );

      return data;
    },
  });

  if (isLoading) return <Spinner />;

  return (
    <>
      {forecast ? (
        <Flex
          width="100%"
          alignItems="center"
          justifyContent="center"
          p={4}
          bgColor={{ base: "gray.100", _dark: "gray.900" }}
          borderRadius="lg"
          gap={4}
        >
          <Flex
            direction="column"
            alignItems="center"
            justifyContent="center"
            minW={200}
          >
            <Text fontWeight="bold" fontSize="3xl">
              {forecast.extremeCondition[0].toUpperCase() +
                forecast.extremeCondition.slice(1)}
            </Text>
            <Image src={getWeatherIcon(forecast.weather)} width={200} />
            <Text fontSize="xl">{dayjs(forecast.date).format("DD MMMM")}</Text>
          </Flex>

          <Text>
            {knownWeather.includes(forecast.weather)
              ? WeatherRecommendations[
                  forecast.weather as keyof typeof WeatherRecommendations
                ]
              : WeatherRecommendations.Other}
          </Text>
        </Flex>
      ) : null}
    </>
  );
};

export default Weather;
