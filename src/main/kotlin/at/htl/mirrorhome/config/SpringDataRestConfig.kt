package at.htl.mirrorhome.config

import at.htl.mirrorhome.mqtt.Actuator
import at.htl.mirrorhome.mqtt.MeasurementValue
import at.htl.mirrorhome.mqtt.Sensor
import at.htl.mirrorhome.user.User
import at.htl.mirrorhome.user.UserEventHandler
import at.htl.mirrorhome.user.calendar.CalendarSource
import at.htl.mirrorhome.user.email.EmailAccount
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.rest.core.config.RepositoryRestConfiguration
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter

@Configuration
class SpringDataRestConfig:RepositoryRestConfigurerAdapter() {

    override fun configureRepositoryRestConfiguration(config:RepositoryRestConfiguration) {
        super.configureRepositoryRestConfiguration(config)
        config.exposeIdsFor(
                Sensor::class.java,
                MeasurementValue::class.java,
                Actuator::class.java,
                User::class.java,
                CalendarSource::class.java,
                EmailAccount::class.java
        )
    }

    @Bean
    open fun userEventHandler(): UserEventHandler {
        return UserEventHandler()
    }

}