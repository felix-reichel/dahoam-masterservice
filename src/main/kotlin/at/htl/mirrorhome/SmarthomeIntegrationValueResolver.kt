package at.htl.mirrorhome

import org.springframework.beans.factory.annotation.Configurable
import org.springframework.beans.factory.annotation.Value

/* Resolves following application.properties bean values */
@Configurable
class SmarthomeIntegrationValueResolver () {
    @Value("\${mqtt.broker}")
    val localBroker: String = ""

    @Value("\${mqtt.broker.domain}")
    val localBrokerDomain: String = ""

    @Value("\${mqtt.clientId}")
    val localClientId: String = ""

    @Value("\${mqtt.htl.broker}")
    val htlBroker: String = ""

    @Value("\${mqtt.htl.broker.domain}")
    val htlBrokerDomain: String = ""

    @Value("\${mqtt.htl.topic}")
    val htlTopic: String = ""

    @Value("\${mqtt.htl.clientId}")
    val htlClientId: String = ""

    @Value("\${mqtt.htl.user}")
    val htlUsername: String = ""

    @Value("\${mqtt.htl.password}")
    val htlPassword: String = ""

}