package at.htl.mirrorhome.speech

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForEntity

@Service
class TextToSpeechService {
    private val log = LoggerFactory.getLogger(TextToSpeechService::class.java)
    private val restTemplate: RestTemplate = RestTemplate()

    @Value("\${tts.api}")
    val speechapi: String = ""

    fun send(msg: String) {
        try {
            val ttsResult: String? = restTemplate.postForEntity<String>("${speechapi}/speaker/speakText", msg).body
            if (!ttsResult.isNullOrBlank()) log.info("TTS Service returned: $ttsResult")
        } catch (ex: Exception) {
            log.info("Sending to Text-to-Speech Service failed.")
            ex.printStackTrace()
        }
    }

}