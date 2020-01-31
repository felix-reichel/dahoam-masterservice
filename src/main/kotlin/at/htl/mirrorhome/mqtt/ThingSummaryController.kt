package at.htl.mirrorhome.mqtt

import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/thingSummary")
class ThingSummaryController(
    val messagingTemplate: SimpMessagingTemplate,
    val actuatorRepository: ActuatorRepository,
    val sensorRepository: SensorRepository
) {
    @GetMapping
    fun getThingSummary(): ThingSummary {
        return ThingSummary(
          actuatorRepository.findAll().toMutableList(),
          sensorRepository.findAll().toMutableList()
        )
    }
}