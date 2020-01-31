package at.htl.mirrorhome.mqtt

import org.springframework.data.repository.CrudRepository

interface MeasurementValueRepository: CrudRepository<MeasurementValue, Long> {
}