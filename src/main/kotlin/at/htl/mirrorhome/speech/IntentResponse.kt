package at.htl.mirrorhome.speech

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Version

@Entity
data class IntentResponse (
        @Id @GeneratedValue val id: Long? = null,
        @Version val version: Long? = null,
        val phrase: String,
        val intent: String
)