package at.htl.mirrorhome.mqtt;

import org.hibernate.StaleObjectStateException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("api/thingMessage")
class ThingMessageController(
    val thingService: ThingService
) {
    private val log = LoggerFactory.getLogger(ThingMessageController::class.java)

    @PostMapping("")
    fun receivedThingMessage(@RequestBody thingMessage: ThingMessage) {
        try {
            thingService.handleThingMessage(thingMessage)
        } catch (ex: StaleObjectStateException) {
            log.info("receivedThingMessage() sensor value was already updated by another Transaction - Failure of optimistic locking")
        }
    }
}

