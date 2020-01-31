package at.htl.mirrorhome.mqtt

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Version

@Entity
data class Actuator (

    @Id @GeneratedValue
    val id: Long = -1,

    @Version
    val version: Long = -1,

    val topic: String = "",
    val qos: Int = 2,
    val contentType: ActuatorContentType = ActuatorContentType.BINARY,
    val type: ActuatorType = ActuatorType.SWITCH,

    var lastValue: Double = 0.0

)