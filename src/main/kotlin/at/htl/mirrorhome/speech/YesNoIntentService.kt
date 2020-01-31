package at.htl.mirrorhome.speech

import at.htl.mirrorhome.MirrorMasterServiceApplication
import at.htl.mirrorhome.user.RegistrationState
import org.springframework.stereotype.Service
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForEntity
import java.time.LocalDateTime

@Service
class YesNoIntentService(
    val messagingTemplate: SimpMessagingTemplate,
    val textToSpeechService: TextToSpeechService
) {
    private val log = LoggerFactory.getLogger(YesNoIntentService::class.java)
    private val restTemplate: RestTemplate = RestTemplate()

    @Value("\${face.api}")
    val faceApi: String = ""

    @Value("\${speech.api}")
    val speechapi: String = ""

    @Value("\${intentparser.api}")
    val intentparserapi: String = ""

    fun handle(jsonObject: JSONObject) {
        log.info("handle() function of YesNoIntentService called.")

        val yesNoResult: Int = jsonObject.getInt("CONFIRMDENY_YesNo")
        var payload = JSONObject()

        /*
           Registration Use Case
        */
        if (MirrorMasterServiceApplication.mainContext.registrationState == RegistrationState.CONFIRM_DENY_REGISTRATION) {
            if (yesNoResult == 1) {
                MirrorMasterServiceApplication.mainContext.registrationState = RegistrationState.STARTING_REGISTRATION_PROCESS
                messagingTemplate.convertAndSend("/topic/command", RegistrationState.STARTING_REGISTRATION_PROCESS)
            } else if (yesNoResult == 0) {

                /* move to RegistrationController */
                val setAutoRegistrationOff: String? = restTemplate.getForEntity("${faceApi}/setAutoRegistration/off", String::class.java).body!!
                log.info("Tried to set auto registration to off - faceApi changed AR. state to ${setAutoRegistrationOff}")

                MirrorMasterServiceApplication.mainContext.registrationState = RegistrationState.REGISTRATION_ABORTED
                messagingTemplate.convertAndSend("/topic/command", RegistrationState.REGISTRATION_ABORTED)
            }
            // reset Speech detection Scope -> todo: write wrapper service
            var scopeAcceptanceResult: String? = (restTemplate.postForEntity<String>("${intentparserapi}/scope", SpeechDetectionScope.LISTEN_FOR_COMMANDS_WITH_KEYWORD)).body
            if (scopeAcceptanceResult != null ) {
                log.info("CET @${LocalDateTime.now()} - scope of speechapi changed ${scopeAcceptanceResult}")
            }
        } else {

            log.info("CONFIRMorDENY_YesNo ${yesNoResult} was received, but there was nothing to process?!")
        }

        payload.put("CONFIRMDENY_YesNo", yesNoResult)


        // send to TTS
        // textToSpeechService.send("Ja/Nein Befehl erhalten.")

        val intent: Intent = Intent(IntentType.YesNoIntent, payload.toString())
        messagingTemplate.convertAndSend("/topic/command", intent)
    }
}