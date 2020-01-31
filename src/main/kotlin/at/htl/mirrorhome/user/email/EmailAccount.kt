package at.htl.mirrorhome.user.email

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class EmailAccount(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = -1,

    val username: String = "",
    val password: String = "",
    val host: String = "",
    val port: Int = 0
)