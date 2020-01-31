package at.htl.mirrorhome.mqtt

import java.time.Instant
import javax.persistence.*

@Entity
data class MeasurementValue(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = -1,

    val timestamp: Instant = Instant.now(),
    val value: Double = 0.0

)