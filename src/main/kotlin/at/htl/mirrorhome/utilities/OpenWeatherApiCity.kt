package at.htl.mirrorhome.utilities

import javax.persistence.*

@Entity
@Table(name = "OpenWeatherApiCity")
data class OpenWeatherApiCity(

    @Id @GeneratedValue
    val id: Long = -1,

    @Version
    val version: Long = -1,

    val cityId: Long = -1,
    val name: String = "",
    val countryISO: String = "",

    val lon: Double = 0.0,
    val lat: Double = 0.0
)

