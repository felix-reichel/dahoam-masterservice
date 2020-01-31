package at.htl.mirrorhome.user.calendar

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class CalendarSource (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = -1,
    val icalUrl: String = "",
    val active: Boolean = false,
    val name: String = ""
)