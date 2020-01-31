package at.htl.mirrorhome.user

import org.springframework.data.repository.CrudRepository

interface UserSettingsRepository: CrudRepository<UserSettings, Long> { }
