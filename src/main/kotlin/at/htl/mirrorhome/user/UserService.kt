package at.htl.mirrorhome.user

import at.htl.mirrorhome.MirrorMasterServiceApplication
import at.htl.mirrorhome.user.calendar.CalendarSource
import at.htl.mirrorhome.user.calendar.CalendarSourceRepository
import at.htl.mirrorhome.user.email.EmailAccount
import at.htl.mirrorhome.user.email.EmailAccountRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UserService(
    val userRepository: UserRepository,
    val userSettingsRepository: UserSettingsRepository,
    val calendarSourceRepository: CalendarSourceRepository,
    val emailAccountRepository: EmailAccountRepository
) {
    private val log = LoggerFactory.getLogger(UserService::class.java)

    @Transactional
    fun addCalendarSource(calendarSource: CalendarSource, userId: Long) {
        val user: User = this.getUserEntity(userId)
        calendarSourceRepository.save(calendarSource)
        user.calendarSources.add(calendarSource)
        userRepository.save(user)
    }

    @Transactional
    fun addEmailAccount(emailAccount: EmailAccount, userId: Long) {
        val user: User = this.getUserEntity(userId)
        emailAccountRepository.save(emailAccount)
        user.emailAccounts.add(emailAccount)
        userRepository.save(user)
    }

    @Transactional
    fun updateCalendarSource(calendarSource: CalendarSource) {
        calendarSourceRepository.save(calendarSource)
    }

    @Transactional
    fun updateEmailAccount(emailAccount: EmailAccount) {
        emailAccountRepository.save(emailAccount)
    }

    @Transactional
    fun updateUserEntity(user: User) {
        /* Okay now it gets dirty ...
        var emailAccounts:  MutableSet<EmailAccount> = user.emailAccounts!!
        var calendarSources: MutableSet<CalendarSource> = user.calendarSources!!


        emailAccounts.stream().forEach { it -> emailAccountRepository.save(it) }
        calendarSources.stream().forEach { it -> calendarSourceRepository.save(it) }
        */
        userRepository.save(user)
    }

   fun getUserEntity(id: Long): User {
        val user: Optional<User> = userRepository.findById(id);

        if (user.isPresent) {
            return user.get()

        } else {
            // TO-DO: implement user not found
            return User()
        }
    }

    fun getLoggedInUserEntity(): User? {
        val loggedInUserId: Long = MirrorMasterServiceApplication.mainContext.activeUser
        val user: Optional<User> = userRepository.findById(loggedInUserId)

        if (user.isPresent) {
            return user.get()

        } else {
            // TO-DO: implement user not found
            return null
        }
    }

    fun getActiveUserEntity(): User? {
        val loggedInUserId: Long = MirrorMasterServiceApplication.mainContext.activeUser
        val user: Optional<User> = userRepository.findById(loggedInUserId)
        log.info("activeUserEntity " + user.toString())

        if (user.isPresent) {
            return user.get()

        } else {
            // TO-DO: implement user not found
            return null
        }
    }

    fun getLastCreatedUserEntity(): User? { return userRepository.findAll().maxBy { it -> it.created } }
}