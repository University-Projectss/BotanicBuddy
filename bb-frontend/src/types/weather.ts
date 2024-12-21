export interface ForecastResponse {
  date: string;
  maxTemperature: number;
  minTemperature: number;
  weather: string;
  windSpeed: number;
  extremeCondition: string;
}

export enum WeatherRecommendations {
  "Clear" = "On a sunny day, ensure your plants are well-hydrated by watering them early in the morning to prevent rapid evaporation and leaf scorching. Avoid wetting the leaves during peak sunlight, as water droplets can act like magnifying glasses and burn the foliage. Monitor soil moisture levels, and provide shade for sensitive plants to protect them from excessive heat. 🌤️",
  "Clouds" = "On a cloudy day, plants may need less water since there is reduced evaporation. However, monitor soil moisture levels to ensure they are not overwatered. It's also a great time to prune or repot plants as the indirect sunlight reduces stress on them. 🌥️",
  "Drizzle" = "Light drizzle can gently hydrate your plants, so additional watering might not be needed. Ensure good drainage to prevent waterlogging, especially for plants in pots. Use this opportunity to clean dust off leaves to improve photosynthesis. 🌨️",
  "Rain" = "Rain is beneficial for most plants, providing them with natural hydration and nutrients. Check that pots have proper drainage to avoid root rot, and secure delicate plants against strong rain that could damage them. 🌧️",
  "Snow" = "Protect plants from snow by brushing off heavy accumulation to prevent branches from breaking. Mulch the soil to insulate roots and cover sensitive plants with burlap or frost cloth. Ensure pots are raised off the ground to prevent freezing. ⛄️",
  "Thunderstorm" = "Strong winds and heavy rain can damage plants, so move potted ones to sheltered areas and secure tall or fragile outdoor plants. Avoid working with plants during storms for safety reasons, and inspect for damage afterward, pruning broken branches if needed. ⛈️",
  "Other" = "Fog increases humidity, which can benefit some plants but also promote fungal diseases. Ensure good air circulation around plants and reduce watering since the soil will dry more slowly. Keep an eye out for mildew or mold, especially on dense foliage. 🌬️",
}
