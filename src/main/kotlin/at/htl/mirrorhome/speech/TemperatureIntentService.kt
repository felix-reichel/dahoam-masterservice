package at.htl.mirrorhome.speech

import at.htl.mirrorhome.mqtt.Sensor
import at.htl.mirrorhome.mqtt.ThingService
import com.google.gson.Gson
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class TemperatureIntentService(
    val messagingTemplate: SimpMessagingTemplate,
    val thingService: ThingService,
    val textToSpeechService: TextToSpeechService
) {
    private val log = LoggerFactory.getLogger(TemperatureIntentService::class.java)

    fun handle(jsonObject: JSONObject) {
        log.info("handle() function of TemperatureIntentService called.")

        var temperatureLocation: String? = "" // waiting for j.fallmann ("REGEX_TemperatureLocation")
        var foundSensor: Sensor? = null
        var payload = JSONObject()

        if(jsonObject.has("KEYWORD_Room")) {
            if (jsonObject.has("KEYWORD_Groundfloor")) {
                temperatureLocation += "E";
            }
            temperatureLocation += jsonObject.getString("NUMBER_Room");
        }

        if (!temperatureLocation.isNullOrBlank()) {
            foundSensor = thingService.getSensorBySearchTerm(temperatureLocation!!, "temperature")
            if (foundSensor != null) {
                payload.put("room", temperatureLocation)
            }
        } else if (temperatureLocation.isNullOrBlank()) {
            foundSensor = thingService.getSensorBySearchTerm("", "temperature")
        }

        if (foundSensor != null) {
            payload.put("foundSensor", Gson().toJson(foundSensor))

            var lastValueOfFoundSensor: Double? = foundSensor.data.maxBy { it.id } ?.value

            textToSpeechService.send("Die Temperatur beträgt $lastValueOfFoundSensor Grad Celsius")
        }

        val intent: Intent = Intent(IntentType.SensorIntent, payload.toString())
        messagingTemplate.convertAndSend("/topic/command", intent)


    }
}