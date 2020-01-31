package at.htl.mirrorhome.speech

import org.springframework.data.repository.CrudRepository

interface IntentDetectionLogRepository: CrudRepository<IntentDetectionLog, Long> {
}