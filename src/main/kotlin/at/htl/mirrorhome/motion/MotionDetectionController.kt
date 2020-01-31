package at.htl.mirrorhome.motion

import at.htl.mirrorhome.MirrorMasterServiceApplication
import at.htl.mirrorhome.mirror.MirrorState
import at.htl.mirrorhome.websocket.WebSocketCommand
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject

@RestController
@RequestMapping("api/motion")
class MotionDetectionController(
	val motionDetectionLogRepository: MotionDetectionLogRepository,
	val messagingTemplate: SimpMessagingTemplate
) {

	private val log = LoggerFactory.getLogger(MotionDetectionController::class.java)
	private val restTemplate: RestTemplate = RestTemplate()

	@Value("\${face.api}")
	val faceApi: String = ""
	
	@GetMapping
	fun motionDetected(): MotionDetectionLog {
		val result = MotionDetectionLog()
		log.info("Received Motion-Detection-Event from MirrorMotionDetection at ${result.timestamp}")

		if (MirrorMasterServiceApplication.mainContext.mirrorState == MirrorState.IDLE) {
			MirrorMasterServiceApplication.mainContext.mirrorState = MirrorState.IDLE_WAITING_FOR_IO
			// restart face detection
			restTemplate.getForObject<Any>("http://${faceApi}/start")
		}
		
		motionDetectionLogRepository.save(result)
		messagingTemplate.convertAndSend("/topic/command", WebSocketCommand.MOTION_DETECTED)
		return result
	}

	@PostMapping
	fun motionDetectedPostEndpoint(): MotionDetectionLog {
		val result = MotionDetectionLog()
		log.info("Received Motion-Detection-Event from MirrorMotionDetection at ${result.timestamp}")
		MirrorMasterServiceApplication.mainContext.mirrorState = MirrorState.IDLE_WAITING_FOR_IO
		motionDetectionLogRepository.save(result)
		messagingTemplate.convertAndSend("/topic/command", WebSocketCommand.MOTION_DETECTED)
		return result
	}
}
