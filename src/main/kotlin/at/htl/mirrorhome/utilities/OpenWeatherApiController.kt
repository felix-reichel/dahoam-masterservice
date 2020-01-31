package at.htl.mirrorhome.utilities

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForEntity

@RestController
@RequestMapping("api/weather")
class OpenWeatherApiController(
    val openWeatherApiService: OpenWeatherApiService
) {
    @Value("\${open.weather.map.api.key}")
    val openWeatherApiKey: String = ""

    val openWeatherApiBaseUri: String = "http://api.openweathermap.org/data/2.5/forecast"

    private val restTemplate: RestTemplate = RestTemplate()
    private val log = LoggerFactory.getLogger(OpenWeatherApiController::class.java)

    @GetMapping
    @RequestMapping("cities")
    fun getAvailableCities(): List<OpenWeatherApiCity> {
        return this.openWeatherApiService.getAvailableCities()
    }

    @GetMapping
    @RequestMapping("maxForecastByCityName")
    fun get5DayForecastByCityName(@RequestParam("cityName") cityName: String): String? {
        var requestedCity: OpenWeatherApiCity? = this.openWeatherApiService.getOpenWeatherApiCityByName(cityName)

        var payload: String? = ""
        var cityId: String = "7872055" // default fallback

        if (requestedCity != null) {
            payload = this.restTemplate.getForEntity<String>("$openWeatherApiBaseUri?id=${requestedCity.cityId}&APPID=$openWeatherApiKey&units=metric").body
        } else if (requestedCity == null) {
            payload = this.restTemplate.getForEntity<String>("$openWeatherApiBaseUri?id=$cityId&APPID=$openWeatherApiKey&units=metric").body
        }

        return payload

    }

    @GetMapping
    @RequestMapping("maxForecastByActiveUser")
    fun get5DayForecastByActiveUser(): String? {
        var payload: String? = ""
        var requestedCityOk = false

        var userSettingsForecastCity: OpenWeatherApiCity? = this.openWeatherApiService.getOpenWeatherApiCityByActiveUser()
        if (userSettingsForecastCity != null) {
            requestedCityOk = true
            payload = this.restTemplate.getForEntity<String>(
                    "$openWeatherApiBaseUri?id=${userSettingsForecastCity.cityId}&APPID=$openWeatherApiKey&units=metric").body
        }

        if (!requestedCityOk) {
            payload = this.restTemplate.getForEntity<String>("$openWeatherApiBaseUri?id=7872055&APPID=$openWeatherApiKey&units=metric").body
        }

        return payload
    }
}