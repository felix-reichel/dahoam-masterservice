package at.htl.mirrorhome.speech

import org.json.JSONArray
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForEntity
import org.springframework.web.client.postForEntity
import java.text.SimpleDateFormat
import java.util.*

@Service
class WeatherIntentService(
    val messagingTemplate: SimpMessagingTemplate,
    val textToSpeechService: TextToSpeechService
) {
    private val restTemplate = RestTemplate()
    private val log = LoggerFactory.getLogger(WeatherIntentService::class.java)

    @Value("\${open.weather.map.api.key}")
    val openWeatherApiKey: String = ""

    val openWeatherApiBaseUri: String = "http://api.openweathermap.org/data/2.5/forecast"

    fun handle(jsonObject: JSONObject, jsonString: String) {
        log.info("handle() function of WeatherIntentService called.")

        var location = "Leonding, AT" // [48.28, 14.25]
        var cityId = "7872055"
        var dateWeather = JSONArray()
        var timespanWeather = JSONArray()
        var payload: String? = ""

        /*
        jsonObject.keys().forEach { k ->
            if (k.equals("DATE_Weather")) { dateWeather = jsonObject.getJSONArray("DATE_Weather") }
            if (k.equals("TIMESPAN_Weather")) { timespanWeather = jsonObject.getJSONArray("TIMESPAN_Weather") }
        }

        if (!dateWeather.isEmpty && !dateWeather.isNull(0)) {
            log.info("WeatherIntentService: user requested weather report for single day")
            val dayString: String = dateWeather.get(0) as String
            val day: Date = SimpleDateFormat("yyyy-MM-dd").parse(dayString)
            log.info("WeatherIntentService: Day ${day.toInstant()}")

            //todo: Request payload from OpenWeatherMap - API
            payload = this.restTemplate.getForObject("$openWeatherApiBaseUri?id=$cityId&APPID=$openWeatherApiKey&units=metric", String::class.java)
            log.info(payload)
        }

        if (!timespanWeather.isEmpty) {
            log.info("WeatherIntentService: user requested multiple days for report")
            timespanWeather.iterator().forEach { it -> log.info("WeatherIntentService: Day ${it}") }

            //todo: Request payload from OpenWeatherMap - API
            payload = this.restTemplate.getForObject("$openWeatherApiBaseUri?id=$cityId&APPID=$openWeatherApiKey&units=metric", String::class.java)
            log.info(payload)
        }
        */

        textToSpeechService.send("Dein Wetterbericht sieht wie folgt aus:")

        val intent: Intent = Intent(IntentType.WeatherIntent,  jsonString)
        messagingTemplate.convertAndSend("/topic/command", intent)

    }

}