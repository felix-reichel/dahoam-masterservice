package at.htl.mirrorhome.user

import at.htl.mirrorhome.ApplicationContextHolder
import at.htl.mirrorhome.websocket.WebSocketCommand
import org.slf4j.LoggerFactory
import org.springframework.data.rest.core.annotation.HandleAfterSave
import org.springframework.data.rest.core.annotation.RepositoryEventHandler
import org.springframework.messaging.simp.SimpMessagingTemplate

@RepositoryEventHandler(User::class)
class UserEventHandler {
    private val log = LoggerFactory.getLogger(UserEventHandler::class.java)

    var messagingTemplate: SimpMessagingTemplate = ApplicationContextHolder.getContext().getBean(SimpMessagingTemplate::class.java)

    @HandleAfterSave
    fun handleAfterSave(user: User) {
        log.info("handleAfterSave - current user entity userId ${user.id} with username ${user.username}")
        messagingTemplate.convertAndSend("/topic/command", WebSocketCommand.USER_DATA_CHANGED)
    }

}