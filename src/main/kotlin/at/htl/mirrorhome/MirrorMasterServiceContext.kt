package at.htl.mirrorhome

import at.htl.mirrorhome.mirror.MirrorState
import at.htl.mirrorhome.user.RegisterModel
import at.htl.mirrorhome.user.RegistrationState
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Configurable
import kotlin.properties.Delegates

@Configurable
class MirrorMasterServiceContext() {

	var mirrorState: MirrorState by Delegates.observable<MirrorState>(MirrorState.IDLE) {
		_, old, new ->
		println("Mirror State changed: $old -> $new")
		// template.sendCommand(WebSocketCommand.ACTUATOR_SUMMARY_CHANGED)
	}

	var registrationState: RegistrationState by Delegates.observable<RegistrationState>(RegistrationState.WAITING_FOR_UNKNOWN_USER_EVENT) {
		_, old, new ->
		println("Registration State changed: $old -> $new")
		// messagingTemplate.convertAndSend("/topic/command", "$new")
	}

	var registrationModel: RegisterModel by Delegates.observable(RegisterModel()) {
		_, old, new ->
		println("RegistrationModel changed: $old -> $new")
	}

	var activeUser: Long by Delegates.observable<Long>(-1) {
		_, old, new ->
		println("Active User Id changed: $old -> $new")
	}

	var listeningForIntent: Boolean by Delegates.observable<Boolean>(false) {
		_, old, new ->
		println("listeningForIntent changed: $old -> $new")
	}

	private val log = LoggerFactory.getLogger(MirrorMasterServiceContext::class.java)
	
	init {
		log.info("MirrorMasterServiceContext initiated and loaded")
	}
}
