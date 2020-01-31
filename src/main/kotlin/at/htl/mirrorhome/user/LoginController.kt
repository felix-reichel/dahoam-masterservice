package at.htl.mirrorhome.user

import at.htl.mirrorhome.MirrorMasterServiceApplication
import at.htl.mirrorhome.mirror.MirrorState
import at.htl.mirrorhome.websocket.WebSocketCommand
import org.slf4j.LoggerFactory
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/login")
class LoginController(
    val userRepository: UserRepository,
    val messagingTemplate: SimpMessagingTemplate
) {
    private val log = LoggerFactory.getLogger(LoginController::class.java)
    private val restTemplate: RestTemplate = RestTemplate()


    @PostMapping()
    fun logInWithCredentials(@RequestBody loginModel: LoginCredentials): User? {
        var user: User? = userRepository.findAll().filter { it -> it.username.equals(loginModel.username) }.getOrNull(0)
        if (user != null && user.password.equals(loginModel.password)) {
            //MirrorMasterServiceApplication.mainContext.activeUser = user.id;
            //MirrorMasterServiceApplication.mainContext.mirrorState = MirrorState.EXECUTING_IN_USER_MODE
            //messagingTemplate.convertAndSend("/topic/command", WebSocketCommand.USER_DETECTED)
            return user
        }
        return null;

    }
}