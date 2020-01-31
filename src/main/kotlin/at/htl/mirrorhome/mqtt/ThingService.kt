package at.htl.mirrorhome.mqtt

import at.htl.mirrorhome.ApplicationContextHolder
import at.htl.mirrorhome.websocket.WebSocketCommand
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.hibernate.StaleObjectStateException
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

@Service
class ThingService (
    val actuatorRepository: ActuatorRepository,
    val sensorRepository: SensorRepository,
    val messagingTemplate: SimpMessagingTemplate,
    val measurementValueRepository: MeasurementValueRepository,
    val mqttLedControllerDemo: MqttLedControllerDemo
): Observable() {
    private val log = LoggerFactory.getLogger(ThingService::class.java)

    // var mqttCommunicator: MqttCommunicator = ApplicationContextHolder.getContext().getBean(MqttCommunicator::class.java)

    var lastActuatorUpdate = ThingMessage()

    fun updateActuator(thingMessage: ThingMessage) {
        /* NOT WORKING
        lastActuatorUpdate = thingMessage
        setChanged()
        notifyObservers(thingMessage)
        log.info("updateActuator -- notifyObservers(thingMessage)")
        */

        // Dirty fix just for project award purposes
        if (thingMessage.value == 1.0) mqttLedControllerDemo.turnLedOn()
        else if (thingMessage.value == 0.0) mqttLedControllerDemo.turnLedOff()

    }

    @Transactional
    fun getActuatorByTopic(topic: String): Actuator? {
        return this.actuatorRepository.findAll().filter { it -> it.topic.equals(topic)}.getOrNull(0)
    }

    @Transactional
    fun getSensorByTopic(topic: String): Sensor? {
        return this.sensorRepository.findAll().filter { it -> it.topic.equals(topic)}.getOrNull(0)
    }

    @Transactional
    fun getSensorBySearchTerm(location: String, sensorType: String): Sensor? {
        var foundSensor: Sensor?

        if(!location.isEmpty()) {
            foundSensor = this.sensorRepository.findAll()
                .filter { 
                    it -> 
                    it.topic.toLowerCase().contains(sensorType.toLowerCase()) 
                    && it.topic.toLowerCase().contains(location.toLowerCase())
                    }
                .getOrNull(0)
        } else {
            foundSensor = this.sensorRepository.findAll()
                .filter { it -> it.topic.toLowerCase().contains(sensorType.toLowerCase()) }.getOrNull(0)
        }
        return foundSensor
    }

    @Transactional
    fun getActuatorBySearchTerm(search: String): Actuator? {
        return this.actuatorRepository.findAll()
                .filter { it -> it.topic.toLowerCase().contains(search.toLowerCase())}
                .getOrNull(0)
    }

    @Transactional
    fun handleMqttMessage(topic: String, message: MqttMessage) {
        log.info("handleMqttMessage() called")
        try {
            var sensor: Sensor? = getSensorByTopic(topic)
            var actuator: Actuator? = getActuatorByTopic(topic)

            var measurementValue = MeasurementValue(-1, Instant.now(), 0.0)

            if (sensor != null) {
                var payloadString: String = String(message.payload)
                if (payloadString.contains("timestamp")) { // is of form
                    measurementValue = MeasurementValue(-1, Instant.now(), JSONObject(payloadString).get("value") as Double)
                } else if(!payloadString.contains("timestamp")) {
                    measurementValue = MeasurementValue(-1, Instant.now(), payloadString.toDouble())
                }

                log.debug("sensor value added: ${measurementValue}")
                measurementValueRepository.save(measurementValue)
                var lastPersistedMeasurementValue: MeasurementValue? = measurementValueRepository.findAll().maxBy { it -> it.timestamp }
                if (lastPersistedMeasurementValue != null) {
                    sensor.data.add(lastPersistedMeasurementValue)
                    sensorRepository.save(sensor)
                    messagingTemplate.convertAndSend("/topic/command", WebSocketCommand.SENSOR_SUMMARY_CHANGED)
                    messagingTemplate.convertAndSend("/topic/command", SingleSensorUpdate(sensor.id))
                }
            }

            if (actuator != null) {
                var mqttMessagePayload: String = String(message.payload)
                var lastActuatorValue: Double = 0.0

                if (mqttMessagePayload.contains("timestamp")) { // handles form of RGB PIR "{"timestamp":2085983557,"value":1}"
                    var jsonPayload: JSONObject = JSONObject(mqttMessagePayload)
                    lastActuatorValue = (jsonPayload.get("value") as Int).toDouble()

                } else if (!mqttMessagePayload.contains("timestamp")) {
                    lastActuatorValue = String(message.payload).toDouble()
                }

                // mqttCommunicator.sendMessageToTopic(topic, lastActuatorValue.toString())
                // DO NOT ----> updateActuator(ThingMessage(topic, Instant.now(), lastActuatorValue))
                log.info("actuator value changed to ${lastActuatorValue}")

                if (actuator.lastValue != lastActuatorValue) {
                    actuator.lastValue = lastActuatorValue
                    actuatorRepository.save(actuator)
                    messagingTemplate.convertAndSend("/topic/command", WebSocketCommand.ACTUATOR_SUMMARY_CHANGED)
                    messagingTemplate.convertAndSend("/topic/command", SingleActuatorUpdate(actuator.id))
                }

            }
        } catch (ex: StaleObjectStateException) {
            log.info("handleThingMessage() sensor value was already updated by another Transaction - Failure of optimistic locking")
        }
    }

    @Transactional
    fun handleThingMessage(thingMessage: ThingMessage) {
        log.info("handleExternalMqttData($thingMessage) called")
        try {
            //todo: timestamp: Int to Instant

            var sensor: Sensor? = getSensorByTopic(thingMessage.topic)
            var actuator: Actuator? = getActuatorByTopic(thingMessage.topic)

            if (sensor != null) {
                var measurementValue = MeasurementValue(-1, Instant.now(), thingMessage.value)
                log.debug("sensor value added: ${measurementValue}")
                measurementValueRepository.save(measurementValue)
                var lastPersistedMeasurementValue: MeasurementValue? = measurementValueRepository.findAll().maxBy { it -> it.timestamp }
                if (lastPersistedMeasurementValue != null) {
                    sensor.data.add(lastPersistedMeasurementValue)
                    sensorRepository.save(sensor)
                    messagingTemplate.convertAndSend("/topic/command", WebSocketCommand.SENSOR_SUMMARY_CHANGED)
                    messagingTemplate.convertAndSend("/topic/command", SingleSensorUpdate(sensor.id))
                }
            }

            if (actuator != null) {
                var lastActuatorValue: Double = thingMessage.value
                // mqttCommunicator.sendMessageToTopic(thingMessage.topic, thingMessage.value.toString())
                updateActuator(thingMessage)
                log.info("actuator value changed to ${lastActuatorValue}")
                actuator.lastValue = thingMessage.value
                actuatorRepository.save(actuator)
                messagingTemplate.convertAndSend("/topic/command", WebSocketCommand.ACTUATOR_SUMMARY_CHANGED)
                messagingTemplate.convertAndSend("/topic/command", SingleActuatorUpdate(actuator.id))
            }
        } catch (ex: StaleObjectStateException) {
            log.info("handleThingMessage() sensor value was already updated by another Transaction - Failure of optimistic locking")
        }

    }
}