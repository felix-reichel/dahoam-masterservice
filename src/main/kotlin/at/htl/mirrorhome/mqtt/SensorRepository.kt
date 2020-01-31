package at.htl.mirrorhome.mqtt

import org.springframework.data.repository.CrudRepository

interface SensorRepository: CrudRepository<Sensor, Long> {

}