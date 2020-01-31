package at.htl.mirrorhome.user

import org.springframework.data.repository.CrudRepository

interface UserRepository: CrudRepository<User, Long> { }
