package at.htl.mirrorhome.speech

import at.htl.mirrorhome.mqtt.Actuator
import at.htl.mirrorhome.mqtt.ThingMessage
import at.htl.mirrorhome.mqtt.ThingService
import com.google.gson.Gson
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class LightIntentService(
    val messagingTemplate: SimpMessagingTemplate,
    val thingService: ThingService,
    val textToSpeechService: TextToSpeechService
) {
    private val log = LoggerFactory.getLogger(LightIntentService::class.java)

    fun handle(jsonObject: JSONObject) {
        log.info("handle() function of TemperatureIntentService called.")

        val stateLight: Int = jsonObject.getInt("STATE_Light")
        var foundActuator: Actuator? = null
        var payload = JSONObject()

        // select any actuator containing 'led' in topicString
        foundActuator = thingService.getActuatorBySearchTerm("led") // should select our demo light
        if (foundActuator != null) {
            thingService.handleThingMessage(ThingMessage(foundActuator.topic, Instant.now(), stateLight.toDouble()))

            foundActuator = thingService.getActuatorBySearchTerm("led")
            payload.put("foundActuator", Gson().toJson(foundActuator))

            var lastValueOfFoundActuator: Double? = foundActuator?.lastValue
            var ttsString: String = ""

            if (lastValueOfFoundActuator == 0.0) {
                ttsString = "Das Licht wird ausgeschaltet"
            }
            else if (lastValueOfFoundActuator == 1.0) {
                ttsString = "Das Licht wird eingeschalten"
            }

            try { textToSpeechService.send(ttsString)
            } catch(ex: Exception) {
                log.info("textToSpeechService seems to be offline...")
            }
        }

        val intent: Intent = Intent(IntentType.ActuatorIntent, payload.toString())
        messagingTemplate.convertAndSend("/topic/command", intent)


    }
}