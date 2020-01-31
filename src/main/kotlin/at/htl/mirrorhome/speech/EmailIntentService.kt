package at.htl.mirrorhome.speech

import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class EmailIntentService(
    val messagingTemplate: SimpMessagingTemplate,
    val textToSpeechService: TextToSpeechService
) {
    private val log = LoggerFactory.getLogger(EmailIntentService::class.java)

    fun handle(jsonObject: JSONObject) {
        log.info("handle() function of EmailIntentService called.")

        textToSpeechService.send("Hier sind deine letzten Emails")

        val intent = Intent(IntentType.EmailIntent, "")
        messagingTemplate.convertAndSend("/topic/command", intent)
    }
}