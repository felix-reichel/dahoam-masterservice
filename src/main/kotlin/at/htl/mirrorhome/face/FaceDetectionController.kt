package at.htl.mirrorhome.face

import at.htl.mirrorhome.MirrorMasterServiceApplication
import at.htl.mirrorhome.mirror.MirrorState
import at.htl.mirrorhome.speech.SpeechDetectionScope
import at.htl.mirrorhome.user.RegistrationState
// import at.dahoam.shared.SpeechDetectionScope -> hosted at Google Cloud Platform's AppEngine
import at.htl.mirrorhome.user.User
import at.htl.mirrorhome.user.UserRepository
import at.htl.mirrorhome.websocket.WebSocketCommand
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForEntity
import java.time.LocalDateTime

@RestController
@RequestMapping("api/face")
class FaceDetectionController(
    val userRepository: UserRepository,
    val messagingTemplate: SimpMessagingTemplate
    )
{
    private val log = LoggerFactory.getLogger(FaceDetectionController::class.java)
    private val restTemplate: RestTemplate = RestTemplate()

    @Value("\${speech.api}")
    val speechapi: String = ""

    @Value("\${face.api}")
    val faceApi: String = ""

    @Value("\${intentparser.api}")
    val intentparserapi: String = ""

    @PostMapping("{userId}")
    fun faceDetected(@PathVariable userId: Long): String {
        log.info("Face with userId ${userId} got detected by Face Detection @${LocalDateTime.now()}")

        var loggedInUser = userRepository.findById(userId)
        if (loggedInUser.isPresent()) {
            MirrorMasterServiceApplication.mainContext.mirrorState = MirrorState.EXECUTING_IN_USER_MODE

            messagingTemplate.convertAndSend("/topic/command", WebSocketCommand.MIRROR_STATE_CHANGED)

            MirrorMasterServiceApplication.mainContext.activeUser = userId

            messagingTemplate.convertAndSend("/topic/command", WebSocketCommand.USER_DETECTED)

            return "+OK ${loggedInUser.get().username} logged in!"
        }
        return "-ERR user with this id not found"


    }

    @GetMapping("unknown")
    fun unknownUserDetected(): String {
        messagingTemplate.convertAndSend("/topic/command", WebSocketCommand.UNKNOWN_USER_DETECTED)
        MirrorMasterServiceApplication.mainContext.registrationState = RegistrationState.CONFIRM_DENY_REGISTRATION

        var scopeAcceptanceResult: String? = (restTemplate.postForEntity<String>("${intentparserapi}/scope", SpeechDetectionScope.CONFIRM_DENY_SCOPE)).body

        if (scopeAcceptanceResult != null ) {
            log.info("unknown user was detected @${LocalDateTime.now()} - scope of speechapi changed ${scopeAcceptanceResult}")

        }
        return "+OK unknown user was detected @${LocalDateTime.now()}"
    }

    @PostMapping("/md5checksum/{md5ProfileCheckSum}")
    fun faceDetectedMD5Profilepicture(@PathVariable md5ProfileCheckSum: String): String {
        log.info("Face with md5ProfileCheckSum ${md5ProfileCheckSum} got detected by Face Detection @${LocalDateTime.now()}")

        var loggedInUser: User? = userRepository.findAll()
                .filter { it -> it.md5Picture.contentEquals(md5ProfileCheckSum) }.getOrNull(0)

        if (loggedInUser != null) {
            MirrorMasterServiceApplication.mainContext.mirrorState = MirrorState.EXECUTING_IN_USER_MODE

            messagingTemplate.convertAndSend("/topic/command", WebSocketCommand.MIRROR_STATE_CHANGED)

            MirrorMasterServiceApplication.mainContext.activeUser = loggedInUser.id

            messagingTemplate.convertAndSend("/topic/command", WebSocketCommand.USER_DETECTED)

            return "+OK ${loggedInUser.username} logged in!"
        }
        return "-ERR user with this id not found"
    }

    fun setAutoRegistrationOff() {
        val setAutoRegistrationOff: String? = restTemplate.getForEntity("${faceApi}/setAutoRegistration/off", String::class.java).body!!
        log.info("Tried to set auto registration to off - faceApi changed AR. state to ${setAutoRegistrationOff}")
    }

    fun setAutoRegistrationOn() {
        val setAutoRegistrationOn: String? = restTemplate.getForEntity("${faceApi}/setAutoRegistration/on", String::class.java).body!!
        log.info("Tried to set auto registration to off - faceApi changed AR. state to ${setAutoRegistrationOn}")
    }

}