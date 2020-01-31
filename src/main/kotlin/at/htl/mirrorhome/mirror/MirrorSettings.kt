package at.htl.mirrorhome.mirror

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Version

@Entity
data class MirrorSettings(

	@Id @GeneratedValue
	val id: Long = -1,

	@Version
	val version: Long = -1,

	// 3 minutes no interaction -> switch to MirrorState.IDLE
	// GUI subscribes Socket topic MirrorState
	val switchToIdleAfterIntMinutes: Int = 3
)
