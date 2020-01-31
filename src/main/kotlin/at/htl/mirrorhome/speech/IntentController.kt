package at.htl.mirrorhome.speech

import at.htl.mirrorhome.MirrorMasterServiceApplication
import at.htl.mirrorhome.mirror.MirrorState
import at.htl.mirrorhome.websocket.WebSocketCommand
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("api/speech")
class IntentController (
    val intentDetectionLogRepository: IntentDetectionLogRepository,
    val messagingTemplate: SimpMessagingTemplate,
    val yesNoIntentService: YesNoIntentService,
    val lightIntentService: LightIntentService,
    val temperatureIntentService: TemperatureIntentService,
    val weatherIntentService: WeatherIntentService,
    val helpIntentService: HelpIntentService,
    val calendarIntentService: CalendarIntentService,
    val emailIntentService: EmailIntentService
) {
    private val log = LoggerFactory.getLogger(IntentController::class.java)

    /*
        POST endpoint for intent
     */
    @PostMapping("intent")
    fun intentDetected(@RequestBody intentObject: String): String {
        log.info("Intent got detected by Speech Detection @${LocalDateTime.now()}")

        /*
            Switch mirror state
         */
        if (MirrorMasterServiceApplication.mainContext.mirrorState == MirrorState.IDLE
                && MirrorMasterServiceApplication.mainContext.activeUser == -1L) {
            MirrorMasterServiceApplication.mainContext.mirrorState = MirrorState.EXECUTING_IN_SYSTEM_MODE
            messagingTemplate.convertAndSend("/topic/command", WebSocketCommand.MIRROR_STATE_CHANGED)
        }

        /*
            Logging Intent Responses
         */
        var intentDetectionLog: IntentDetectionLog = IntentDetectionLog(-1, -1, LocalDateTime.now(), intentObject)
        intentDetectionLogRepository.save(intentDetectionLog)

        /*
            Intent Parsing refs: https://gitlab.htl-leonding.ac.at/MirrorHome/Core/tree/master/src/speech/IntentParser/
        */
        val jsonString: String = intentObject
        val jsonObject: JSONObject = JSONObject(jsonString)
        val intentKey: String = jsonObject.getString("intent")
        var containsWakeupWord = false

        jsonObject.keySet().forEach { key ->
            if (key == ("containsWakeupWord")) {
                containsWakeupWord = jsonObject.getBoolean("containsWakeupWord")
                log.info("Parsed value of containsWakeupWord: $containsWakeupWord")
            }
        }

        if (intentKey == "YesNoIntent") yesNoIntentService.handle(jsonObject)

        if (containsWakeupWord || MirrorMasterServiceApplication.mainContext.listeningForIntent) {
            when (intentKey) {
                "YesNoIntent" -> { yesNoIntentService.handle(jsonObject) }
                "LightIntent" -> { lightIntentService.handle(jsonObject) }
                "CalendarIntent" -> {calendarIntentService.handle(jsonObject) }
                "EmailIntent" -> { emailIntentService.handle(jsonObject) }
                "WeatherIntent" -> { weatherIntentService.handle(jsonObject, jsonString) }
                "HelpIntent" -> { helpIntentService.handle() }
                "TemperatureIntent" -> { temperatureIntentService.handle(jsonObject) }
                "RegistrationIntent" -> { messagingTemplate.convertAndSend("/topic/command", Intent(IntentType.RegistrationIntent, null)) }
                else -> {
                    log.info("Intent type not supported yet. Type $intentKey")
                }
            }
        }

        return "+OK from IntentController"

    }

    @GetMapping("intent/latest")
    fun getLatestIntent(): String {
        log.info("getLatestIntent() only in use whilst MasterService is not parsing intents fully!")

        val lastIntent = intentDetectionLogRepository.findAll().last()

        if (lastIntent != null ) {
            return lastIntent.intentResponseString;
        } else {
            return "-ERR getLatestIntent()"
        }
    }

    @GetMapping("intent/wakeUp")
    fun setWakeUpWordListening(): String {
        MirrorMasterServiceApplication.mainContext.listeningForIntent = true
        return "+listeningForIntent"
    }

}
