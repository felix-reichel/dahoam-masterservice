package at.htl.mirrorhome.user

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Version

@Entity
data class UserSettings (
		@Id @GeneratedValue
		val id: Long = -1,

		@Version
		val version: Long = -1,

		val invidualWakeUpCommand: String = "Spieglein Spieglein",

		val cityZipForWeatherForecast: Long = 7872055 // Leonding, AT

		// bidirectional mappings -> avoid circle-references! (e.g. toString(), ..)
		// @OneToOne(mappedBy="userSettings") val user: User? = null
)
