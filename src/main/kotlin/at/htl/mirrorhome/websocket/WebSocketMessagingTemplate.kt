package at.htl.mirrorhome.websocket

import org.slf4j.LoggerFactory
import org.springframework.messaging.simp.SimpMessagingTemplate

class WebSocketMessagingTemplate(
    val template: SimpMessagingTemplate
) {

    private val path: String = "/ws"
    private val log = LoggerFactory.getLogger(WebSocketMessagingTemplate::class.java)


    fun sendCommand(command: WebSocketCommand) {
        template.convertAndSend(path, command)
        log.info("Sent via Websocket on path: " +  path + " command: " + command)
    }

    fun sendPayload(payload: WebSocketData) {
        template.convertAndSend(path, payload)
        log.info("Payload via Websocket on path: " + path + " payload: " + payload.toString())
    }
}