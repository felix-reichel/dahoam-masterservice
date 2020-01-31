package at.htl.mirrorhome.mqtt

import javax.persistence.*

@Entity
data class Sensor (

    @Id @GeneratedValue
    val id: Long = -1,

    @Version
    val version: Long = -1,

    val topic: String = "",

    @Column(nullable = true)
    @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    val data: MutableSet<MeasurementValue> = mutableSetOf<MeasurementValue>(),

    val type: SensorType = SensorType.TEMPERATURE
)