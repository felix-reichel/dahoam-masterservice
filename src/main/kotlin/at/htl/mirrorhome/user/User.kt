package at.htl.mirrorhome.user

import at.htl.mirrorhome.user.calendar.CalendarSource
import at.htl.mirrorhome.user.email.EmailAccount
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name="UserAccount")
data class User(

	@Id @GeneratedValue
	val id: Long = -1,

	@Version
	val version: Long = -1,

	val username: String = "",
	val email: String = "",
	val password: String = "", // @JsonIgnore used as fallback
	val md5Picture: String = "",

	@Enumerated(EnumType.STRING)
	val userRole: UserRole = UserRole.USER, // @JsonIgnre

	val firstname: String = "",
	val lastname: String = "",

	val created: Instant = Instant.now(),

	@OneToOne(cascade = [CascadeType.ALL])
	@JoinColumn(name = "usersettings_id")
	val userSettings: UserSettings? = null, // @JsonIgnore e.g. lazy fetch

	@Column(nullable = true)
	@OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
	val emailAccounts: MutableSet<EmailAccount> = mutableSetOf<EmailAccount>(),

	@Column(nullable = true)
	@OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
	val calendarSources: MutableSet<CalendarSource> = mutableSetOf<CalendarSource>()

)
