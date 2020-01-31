package at.htl.mirrorhome.user.email

import at.htl.mirrorhome.user.User
import at.htl.mirrorhome.user.UserService
import at.htl.mirrorhome.user.calendar.Appointment
import at.htl.mirrorhome.user.calendar.CalendarFetcher
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/email")
class EmailController(
        val userService: UserService

) {
    private val log = LoggerFactory.getLogger(EmailController::class.java)

    @GetMapping("emailsByActiveUser")
    fun getEmailsByActiveUser(): List<Email>? {
        var activeUser: User? = userService.getActiveUserEntity()
        return if (activeUser != null) {
            EmailFetcher.fetch(activeUser.emailAccounts.toList()).takeLast(5)
        } else null
    }

}