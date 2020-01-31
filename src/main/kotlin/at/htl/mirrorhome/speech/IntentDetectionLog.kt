package at.htl.mirrorhome.speech

import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Version
import javax.persistence.Column

@Entity
data class IntentDetectionLog(
    @Id @GeneratedValue val id: Long? = null,
    @Version val version: Long? = null,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    @Column(length=2048)
    val intentResponseString: String = ""
)