package at.htl.mirrorhome

import at.htl.mirrorhome.mqtt.*
import at.htl.mirrorhome.user.*
import at.htl.mirrorhome.user.calendar.CalendarSource
import at.htl.mirrorhome.user.email.EmailAccount
import at.htl.mirrorhome.utilities.DBDataLoader
import at.htl.mirrorhome.utilities.DBDataLoaderRepository
import at.htl.mirrorhome.utilities.InetHelper
import at.htl.mirrorhome.utilities.OpenWeatherApiZipCodeImporter
import at.htl.mirrorhome.websocket.WebSocketCommand
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.core.task.TaskExecutor
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.web.client.RestTemplate
import java.net.InetAddress
import java.time.Instant
import java.util.*
import javax.annotation.PostConstruct

fun main(args: Array<String>) { runApplication<MirrorMasterServiceApplication>(*args) }

@EnableScheduling
@SpringBootApplication
open class MirrorMasterServiceApplication {
	companion object {
		var mainContext = MirrorMasterServiceContext()
	}

	private val log = LoggerFactory.getLogger(MirrorMasterServiceApplication::class.java)

	/* Should not be resolved here */
	@Value("\${mqtt.broker.domain}")
	val localBrokerDomain: String = ""

	@Value("\${mqtt.htl.broker}")
	val htlBroker: String = ""

	@Value("\${mqtt.htl.broker.domain}")
	val htlBrokerDomain: String = ""

	@Value("\${mqtt.htl.topic}")
	val htlTopic: String = ""

	@Value("\${mqtt.htl.user}")
	val htlUsername: String = ""

	@Value("\${mqtt.htl.password}")
	val htlPassword: String = ""

	@Value("\${face.api}")
	val faceApi: String = ""

	@Bean
    open fun init(
			userRepository: UserRepository,
			actuatorRepository: ActuatorRepository,
			sensorRepository: SensorRepository,
			measurementValueRepository: MeasurementValueRepository,
			messagingTemplate: SimpMessagingTemplate,
			mqttLedControllerDemo: MqttLedControllerDemo,
			DBDataLoaderRepository: DBDataLoaderRepository
	) = CommandLineRunner {
		if (DBDataLoaderRepository.count() == 0L) { // only run db demo inserts once
			val dbDataLoader = DBDataLoader(-1, -1, Instant.now())
			DBDataLoaderRepository.save(dbDataLoader)

			val user1 = User(-1L, -1L, "jonas", "jonas.fallmann@gmx.at", "secret", "d04c3abebd2f74af40840fc8cfc39bb7", UserRole.SUPERUSER, "Jonas", "Fallmann", Instant.now(), UserSettings())
			user1.calendarSources.add(CalendarSource(-1L, "https://calendar.google.com/calendar/ical/civedg%40gmail.com/public/basic.ics", true, "DemoSource"))
			user1.emailAccounts.add(EmailAccount(-1L, "dahoamTestEmail@gmx.at", "asdfghjklöä", "pop.gmx.net"))
			val user2 = User(-1L, -1L, "markus", "markus.premstaller@gmx.at", "secret", "314fb56074a888f7c4ef64103c0d6fc6", UserRole.SUPERUSER, "Markus", "Premstaller", Instant.now(), UserSettings())
			val user3 = User(-1L, -1L, "felix", "f.reichel@gmx.at", "", "73ba9577c41f118d1390daf4c6c4a2a3", UserRole.SUPERUSER, "Felix", "Reichel", Instant.now(), UserSettings())

			userRepository.save(user1)
			userRepository.save(user2)
			userRepository.save(user3)

			log.info("Users found with findAll():")
			log.info("-------------------------------")
			userRepository.findAll().forEach { log.info(it.toString()) }
			log.info("")

			val demoLed = Actuator(-1L, -1L, "smarthome/room1/led", 2, ActuatorContentType.BINARY, ActuatorType.LIGHT, 0.0)
			actuatorRepository.save(demoLed)

			var inetHelper: InetHelper = InetHelper()
			var ip: InetAddress = inetHelper.getLANAddress()
			log.info("current ip: " + ip.hostAddress)

			// Sensor registry
			val schoolHumidity = Sensor(-1L, -1L, "htlleonding/firstfloor/e582/pc/humidity", mutableSetOf<MeasurementValue>(), SensorType.HUMIDITY)
			sensorRepository.save(schoolHumidity)

			val schoolCo2 = Sensor(-1L, -1L, "htlleonding/firstfloor/e582/pc/co2", mutableSetOf<MeasurementValue>(), SensorType.CO2CONCENTRATION)
			sensorRepository.save(schoolCo2)

			val schoolTemperature = Sensor(-1L, -1L, "htlleonding/firstfloor/e582/pc/temperature", mutableSetOf<MeasurementValue>(), SensorType.TEMPERATURE)
			sensorRepository.save(schoolTemperature)

			val schoolOutdoorWindSpeed = Sensor(-1L, -1L, "htlleonding/outdoor/weather/actual/wind_speed", mutableSetOf<MeasurementValue>(), SensorType.WIND_SPEED)
			sensorRepository.save(schoolOutdoorWindSpeed)

			val schoolDB = Sensor(-1L, -1L, "htlleonding/firstfloor/e582/pc/db", mutableSetOf<MeasurementValue>(), SensorType.DB)
			sensorRepository.save(schoolDB)

			val testTemp = Sensor(-1L, -1L, "smarthome/room47/temperature", mutableSetOf<MeasurementValue>(), SensorType.TEMPERATURE);
			sensorRepository.save(testTemp);

			val testHum = Sensor(-1L, -1L, "smarthome/room47/humidity", mutableSetOf<MeasurementValue>(), SensorType.HUMIDITY);
			sensorRepository.save(testHum);

			/*val testPir = Actuator(-1, -1, "smarthome/room47/pir", 2, ActuatorContentType.BINARY, ActuatorType.LIGHT, 0.0)
			actuatorRepository.save(testPir)*/

			// send thing summary changed event
			messagingTemplate.convertAndSend("/topic/command", WebSocketCommand.THING_SUMMARY_CHANGED);

			log.info("Actuators found with findAll()")
			log.info("-------------------------------")
			actuatorRepository.findAll().forEach { log.info(it.toString()) }
			log.info("")

		}

		val thingService = ThingService(actuatorRepository, sensorRepository, messagingTemplate, measurementValueRepository, mqttLedControllerDemo)
		val localMqttCommunicator = MqttCommunicator("http://127.0.0.1:1883", "#", "", "", 0, thingService, localBrokerDomain)

		log.info("injected school broker fields are $htlBrokerDomain - $htlBroker - $htlUsername - $htlPassword - $htlTopic")
		val schoolMqttCommunicator = MqttCommunicator("http://$htlBrokerDomain:1883", htlTopic, htlUsername, htlPassword,0, thingService, htlBrokerDomain)

		thingService.addObserver(localMqttCommunicator)
		thingService.addObserver(schoolMqttCommunicator)
		MirrorMasterServiceApplication.mainContext.activeUser = 2

		OpenWeatherApiZipCodeImporter.import() // cp resources/city.list.json to /usr/local/bin/city.list.json

		val restTemplate: RestTemplate = RestTemplate()

		try {
			val enableFacerecognition: String? = restTemplate.getForEntity("${faceApi}/start", String::class.java).body!!
			log.info("Tried to enable facerecognition. Result: ${enableFacerecognition}")
		} catch (ex:Exception) {
			log.info("facerecognition start request failed.")
		}
    }
    
	@PostConstruct
	fun applicationStarted() {
		TimeZone.setDefault(TimeZone.getTimeZone("CET"))
	}
    
	@Bean
	open fun asyncExecutor(): ThreadPoolTaskExecutor {
		var executor = ThreadPoolTaskExecutor()
		executor.corePoolSize = 2
		executor.maxPoolSize = 2
		executor.setQueueCapacity(500)
		executor.setThreadNamePrefix("async-executor-")
		executor.initialize()
		return executor
	}

	@Bean
	open fun taskExecutor(): TaskExecutor {
		var executor = ThreadPoolTaskExecutor()
		executor.corePoolSize = 4
		executor.maxPoolSize = 8
		executor.setQueueCapacity(30000)
		executor.setThreadNamePrefix("task-executor-")
		executor.initialize()
		return executor
	}

	/*
		// Demo sensor if needed
		val sensorData1: MeasurementValue = MeasurementValue(-1L, Instant.now(), 99.7)
		measurementValueRepository.save(sensorData1)
		val sensorData2: MeasurementValue = MeasurementValue(-1L, Instant.now().minusMillis(1000), 96.5)
		measurementValueRepository.save(sensorData2)
		val sensorData3: MeasurementValue = MeasurementValue(-1L, Instant.now().minusMillis(2000), 92.0)
		measurementValueRepository.save(sensorData3)
		val measurementValues: MutableSet<MeasurementValue> = measurementValueRepository.findAll().toMutableSet()

		val demoTemperature = Sensor(-1L, -1L, "smarthome/outdoor/temp", measurementValues, SensorType.TEMPERATURE)
		sensorRepository.save(demoTemperature)
	*/

}
