package at.htl.mirrorhome.user.email

import org.springframework.data.repository.CrudRepository

interface EmailAccountRepository: CrudRepository<EmailAccount, Long> {
}