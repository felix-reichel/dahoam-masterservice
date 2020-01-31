package at.htl.mirrorhome.mqtt

import at.htl.mirrorhome.websocket.WebSocketCommand
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/mqttsensordemo")
class MqttSensorControllerDemo(
    val messagingTemplate: SimpMessagingTemplate
) {
    val topic: String = "smarthome/outdoor/temp"
    val qos: Int = 2
    @Value("\${mqtt.broker}")
    val broker: String = ""
    @Value("\${mqtt.clientId}")
    val clientId: String = ""
    val persistence: MemoryPersistence = MemoryPersistence()
    private val log = LoggerFactory.getLogger(MqttSensorControllerDemo::class.java)

    fun startSubscription() {
        log.info("startSubscription() called MqttSensorControllerDemo")
        var sampleClient: MqttClient = MqttClient(broker, clientId, persistence)
        var connOpts: MqttConnectOptions = MqttConnectOptions()
        log.info("Connecting to broker: "+broker);
        sampleClient.connect(connOpts);
        log.info("Connected - trying to start subscription");

        /*
        * CountDownLatch receivedSignal = new CountDownLatch(10);
subscriber.subscribe(EngineTemperatureSensor.TOPIC, (topic, msg) -> {
    byte[] payload = msg.getPayload();
    // ... payload handling omitted
    receivedSignal.countDown();
});
receivedSignal.await(1, TimeUnit.MINUTES);
*/

        /*

        try {
            var sampleClient: MqttClient = MqttClient(broker, clientId, persistence)
            var connOpts: MqttConnectOptions = MqttConnectOptions()
            var content: String = "1"
            connOpts.setCleanSession(true);
            log.info("Connecting to broker: "+broker);
            sampleClient.connect(connOpts);
            log.info("Connected");
            log.info("Publishing message: " + content);
            var message: MqttMessage = MqttMessage(content.toByteArray())
            message.setQos(qos);
            sampleClient.publish(topic, message);
            // sampleClient.sub
            System.out.println("Message published");

            messagingTemplate.convertAndSend("/topic/command", WebSocketCommand.DEMO_LIGHT_ON)

            sampleClient.disconnect();
            log.info("Disconnected");
        } catch (me: MqttException) {
            log.info(me.message);
        }
         */
    }

}