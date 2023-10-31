package com.weather.api.controller


import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/api/weather-forecast")
class WeatherController(private val webClient: WebClient) {

	 companion object {
        private val DAY_NAMES = arrayOf(
            "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
        )
		
     	private const val WEATHER_API_URL = "https://api.weather.gov/gridpoints/MLB/33,70/forecast" 
		private const val RESPONSE_PROPERTIES = "properties"
		private const val START_TIME = "startTime"
		private const val PERIODS = "periods"
        private const val DAY_NAME_KEY = "day_name"
        private const val TEMP_HIGH_KEY = "temp_high_celsius"
        private const val FORECAST_BLURP_KEY = "forecast_blurp"
		private const val TEMPARATURE = "temperature"
		private const val DETAILED_FORECAST = "detailedForecast"
		 
    }
	
    @GetMapping("/today")
    fun getWeatherForecast(): Mono<Map<String, Any>> {
        return webClient
            .get()
            .uri(WEATHER_API_URL)
            .retrieve()
            .bodyToMono(Map::class.java)
            .map { response ->
                val dailyForecast = (response[RESPONSE_PROPERTIES] as Map<*, *>)[PERIODS] as List<Map<*, *>>
                val currentDayForecast = dailyForecast.first()

                val dateTimeString = currentDayForecast[START_TIME] as String
                val dateTime = ZonedDateTime.parse(dateTimeString, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                val dayOfWeek = DAY_NAMES[dateTime.dayOfWeek.value % 7]

                mapOf(
                    "daily" to listOf(
                        mapOf(
                            DAY_NAME_KEY to dayOfWeek,
                            TEMP_HIGH_KEY to currentDayForecast[TEMPARATURE],
                            FORECAST_BLURP_KEY to currentDayForecast[DETAILED_FORECAST]
                        )
                    )
                )
            }
    }
	
	@GetMapping("/default")
    fun getDefaultWeatherForecast(): Mono<String> {
        return webClient
            .get()
            .uri(WEATHER_API_URL)
            .retrieve()
            .bodyToMono(String::class.java)
    }
}
