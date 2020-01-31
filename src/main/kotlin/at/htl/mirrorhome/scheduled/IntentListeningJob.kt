package at.htl.mirrorhome.scheduled

import at.htl.mirrorhome.MirrorMasterServiceApplication
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class IntentListeningJob {

    @Value("\${intent.listening.job}")
    val intentListeningFixedRate: Long = 0

    private val log = LoggerFactory.getLogger(IntentListeningJob::class.java)

    @Scheduled(fixedRate = 5000)
    fun update() {
        if (MirrorMasterServiceApplication.mainContext.listeningForIntent) {
            MirrorMasterServiceApplication.mainContext.listeningForIntent = false
            log.info("Turned of IntentListening in Job - Reason: WakeUpCommand outdated!")
        }
    }

}