package at.htl.mirrorhome.mqtt

import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.slf4j.LoggerFactory
import java.net.InetAddress
import java.net.URI
import java.net.URISyntaxException
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import java.util.*

class MqttCommunicator @Throws(MqttException::class, MqttSecurityException::class)
constructor(uri: URI, val topic: String, val username: String, val password: String, val qoS: Int,
    val thingService: ThingService, val brokerDomain: String)

    : Observer, MqttCallback, IMqttActionListener {

    private val client: MqttAsyncClient
    private var isSubscribed: Boolean = false
    // private val client: MqttClient
    private val clientId: String = "Dahoam SmartMirror Paho Client"

    private val log = LoggerFactory.getLogger(MqttCommunicator::class.java)

    @Throws(MqttException::class, URISyntaxException::class, MqttSecurityException::class)
    constructor(uri: String, topic: String, username: String, password: String, qoS: Int, thingService: ThingService, brokerDomain: String) :
            this(URI(uri), topic, username, password, qoS, thingService, brokerDomain) {}

    init {
        log.info("trying to resolve $brokerDomain ...")
        var address: InetAddress = InetAddress.getByName(brokerDomain);
        log.info("Resolved mqtt broker domain ${address.hostAddress}");
        var host = String.format("tcp://%s:%d", address.hostAddress, uri.getPort())
        val conOpt = MqttConnectOptions()
        conOpt.isCleanSession = true

        if (!username.isNullOrBlank() && !password.isNullOrBlank()) {
            log.info("using username $username and passwort $password to connect via mqtt")
            conOpt.userName = username
            conOpt.password = password.toCharArray()
        }

        this.client = MqttAsyncClient(host, clientId, MemoryPersistence())
        // this.client = MqttClient(host, clientId, MemoryPersistence())
        this.client.setCallback(this)
        this.client.connect(conOpt, null, this)
        // this.client.connect(conOpt)


        log.info("generic mqtt subscriber connected ...")
        // this.client.subscribe(this.topic, qoS)
        // this.client.subscribe(this.topic, qoS)
        log.info("${address.hostAddress} generic subscriber connected to topic $topic with QoS: $qoS")
    }

    override fun update(o: Observable?, arg: Any?) {
        try {
            var observedThingMessage: ThingMessage = (o as ThingService).lastActuatorUpdate
            log.info("sendMessageToTopic ${observedThingMessage}")
            sendMessageToTopic(observedThingMessage.topic, observedThingMessage.value.toString())
        } catch(ex: Exception) {
            log.info("update of Observer - MqttCommunicator failed...")
            ex.printStackTrace()
        }
    }

    @Throws(MqttException::class)
    fun sendMessage(payload:String) {
        val message = MqttMessage(payload.toByteArray())
        message.qos = qoS
        this.client.publish(this.topic, message) // Blocking publish
    }

    @Throws(MqttException::class)
    fun sendMessageToTopic(topic: String, payload: String) {
        val message = MqttMessage(payload.toByteArray())
        message.qos = qoS
        this.client.publish(topic, message) // Blocking publish
    }

    override fun connectionLost(cause:Throwable) {
        log.info("Connection to Mqtt-Broker $brokerDomain:1883 failed!")
        cause.printStackTrace()
    }

    override fun deliveryComplete(token:IMqttDeliveryToken) {

    }

    @Throws(MqttException::class)
    override fun messageArrived(topic: String, message: MqttMessage) {
        println(String.format("[%s] %s", topic, String(message.payload)))
        log.info("received msg: ${String(message.payload)}")
        thingService.handleMqttMessage(topic, message)
    }


    @Throws(MqttException::class)
    override fun onSuccess(asyncActionToken: IMqttToken?) {
        if (!this.isSubscribed) {
            this.client.subscribe(this.topic, qoS, null, this)
            // this.client.subscribe(this.topic, qoS)
            log.info("generic subscriber connected onSuccess to topic $topic with QoS: $qoS")
            this.isSubscribed = true
        }

    }

    @Throws(MqttException::class)
    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
        log.info("connection failed (IMqttActionListener)... ${exception?.localizedMessage}")
    }

}