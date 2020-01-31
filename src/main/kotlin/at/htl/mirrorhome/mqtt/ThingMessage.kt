package at.htl.mirrorhome.mqtt

import java.time.Instant


data class ThingMessage(
    val topic: String = "",
    val timestamp: Instant = Instant.now(),
    val value: Double = 0.0
)