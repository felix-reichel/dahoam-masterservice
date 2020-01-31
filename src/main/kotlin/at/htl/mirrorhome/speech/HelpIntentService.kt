package at.htl.mirrorhome.speech

import org.slf4j.LoggerFactory
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class HelpIntentService(
    val messagingTemplate: SimpMessagingTemplate,
    val textToSpeechService: TextToSpeechService
) {
    private val log = LoggerFactory.getLogger(HelpIntentService::class.java)

    fun handle() {
        log.info("handle() function of HelpIntentService called.")
        
        textToSpeechService.send("Die verfügbaren Kommandos werden angezeigt.")

        messagingTemplate.convertAndSend("/topic/command", Intent(IntentType.HelpIntent, null))
    }
}