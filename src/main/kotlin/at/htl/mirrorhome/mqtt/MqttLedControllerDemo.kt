package at.htl.mirrorhome.mqtt

import at.htl.mirrorhome.websocket.WebSocketCommand
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.messaging.simp.SimpMessagingTemplate

@RestController
@RequestMapping("api/mqttleddemo")
class MqttLedControllerDemo(
    val messagingTemplate: SimpMessagingTemplate
) {
    val topic: String = "smarthome/room1/led"
    val qos: Int = 2
    @Value("\${mqtt.broker}")
    val broker: String = ""
    @Value("\${mqtt.clientId}")
    val clientId: String = ""
    val persistence: MemoryPersistence = MemoryPersistence()
    private val log = LoggerFactory.getLogger(MqttLedControllerDemo::class.java)

    @GetMapping("on")
    fun turnLedOn() {
        log.info("turnLedOn() called MqttLedControllerDemo")
        /*
        *   Temporary - Move to service layer!
        */
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

    }

    @GetMapping("off")
    fun turnLedOff() {
        try {
            var sampleClient: MqttClient = MqttClient(broker, clientId, persistence)
            var connOpts: MqttConnectOptions = MqttConnectOptions()
            var content: String = "0"
            connOpts.setCleanSession(true);
            log.info("Connecting to broker: "+broker);
            sampleClient.connect(connOpts);
            log.info("Connected");
            log.info("Publishing message: " + content);
            var message: MqttMessage = MqttMessage(content.toByteArray())
            message.setQos(qos);
            sampleClient.publish(topic, message);
            System.out.println("Message published");

            messagingTemplate.convertAndSend("/topic/command", WebSocketCommand.DEMO_LIGHT_OFF)

            sampleClient.disconnect();
            log.info("Disconnected");
        } catch (me: MqttException) {
            log.info(me.message);
        }

    }
}