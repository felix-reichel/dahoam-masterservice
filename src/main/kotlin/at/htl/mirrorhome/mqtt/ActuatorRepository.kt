package at.htl.mirrorhome.mqtt

import org.springframework.data.repository.CrudRepository

interface ActuatorRepository: CrudRepository<Actuator, Long> {
}