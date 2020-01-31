package at.htl.mirrorhome.motion

import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Version

@Entity
data class MotionDetectionLog(
	@Id @GeneratedValue val id: Long? = null,
	@Version val version: Long? = null,
	val timestamp: LocalDateTime = LocalDateTime.now()
)
