package at.htl.mirrorhome.user

import at.htl.mirrorhome.MirrorMasterServiceApplication
import at.htl.mirrorhome.user.calendar.CalendarSource
import at.htl.mirrorhome.user.email.EmailAccount
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("api/user")
class UserController(
        val userService: UserService
    ) {
    private val log = LoggerFactory.getLogger(UserController::class.java)

    @GetMapping("{id}")
    fun getUserEntity(@PathVariable("id") id: Long): User {
        return userService.getUserEntity(id)
    }

    @PutMapping("{id}")
    fun updateUserEntity(@RequestBody user: User, @PathVariable("id") id: Long): ResponseEntity<Any?> {
        userService.updateUserEntity(user)
        return ResponseEntity.ok("user updated")
    }

    @PostMapping("{userId}/addEmailAccount")
    fun addEmailAccountToUser(@RequestBody emailAccount: EmailAccount, @PathVariable("userId") userId: Long): ResponseEntity<Any?> {
        userService.addEmailAccount(emailAccount, userId)
        return ResponseEntity.ok("user emailAccount added")
    }

    @PostMapping("{userId}/addCalendarSource")
    fun addCalendarSourceToUser(@RequestBody calendarSource: CalendarSource, @PathVariable("userId") userId: Long): ResponseEntity<Any?> {
        userService.addCalendarSource(calendarSource, userId)
        return ResponseEntity.ok("user calendarSource added")
    }

    @GetMapping("loggedInUser")
    fun getLoggedInUserEntity(): User? {
        return userService.getLoggedInUserEntity()
    }

    @GetMapping("lastCreated")
    fun getLastCreatedUserEntity(): User? {
        return userService.getLastCreatedUserEntity()
    }

    // also possible to use PUT /emailAccounts/{id} from CalendarSourceRepository implementing CRUD Rest Ops
    @PutMapping("updateEmailAccount")
    fun updateEmailAccount(@RequestBody emailAccount: EmailAccount): ResponseEntity<Any?> {
        userService.updateEmailAccount(emailAccount)
        return ResponseEntity.ok("emailAccount updated")
    }

    // also possible to use PUT /calendarSources/{id} from CalendarSourceRepository implementing CRUD Rest Ops
    @PutMapping("updateCalendarSource")
    fun updateCalendarSource(@RequestBody calendarSource: CalendarSource): ResponseEntity<Any?> {
        userService.updateCalendarSource(calendarSource)
        return ResponseEntity.ok("calendarSource updated")
    }

    // also possible to use DELETE /emailAccounts/{id} from CalendarSourceRepository implementing CRUD Rest Ops
    @DeleteMapping("deleteEmailAccount")
    fun deleteEmailAccount(@RequestBody emailAccount: EmailAccount) {
        userService.emailAccountRepository.delete(emailAccount)
    }

    // also possible to use DELETE /calendarSources/{id} from CalendarSourceRepository implementing CRUD Rest Ops
    @DeleteMapping("deleteCalendarSource")
    fun deleteEmailAccount(@RequestBody calendarSource: CalendarSource) {
        userService.calendarSourceRepository.delete(calendarSource)
    }

}