package at.htl.mirrorhome.user.calendar

import at.htl.mirrorhome.user.User
import at.htl.mirrorhome.user.UserService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/calendar")
class CalendarController(
    val userService: UserService

) {
    private val log = LoggerFactory.getLogger(CalendarController::class.java)

    @GetMapping("appointmentsByActiveUser")
    fun getAppointmentsByActiveUser(): List<Appointment>? {
        var activeUser: User? = userService.getActiveUserEntity()
        return if (activeUser != null) CalendarFetcher.fetch(activeUser.calendarSources.toList()) else null
    }

}