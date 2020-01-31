package at.htl.mirrorhome.speech

import at.htl.mirrorhome.mqtt.ThingService
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class CalendarIntentService(
    val messagingTemplate: SimpMessagingTemplate,
    val textToSpeechService: TextToSpeechService
    ) {
    private val log = LoggerFactory.getLogger(CalendarIntentService::class.java)

    fun handle(jsonObject: JSONObject) {
        log.info("handle() function of CalendarIntentService called.")

        textToSpeechService.send("Hier sind deine nächsten Kalendereinträge")

        val intent = Intent(IntentType.CalendarIntent, "")
        messagingTemplate.convertAndSend("/topic/command", intent)
    }
}
