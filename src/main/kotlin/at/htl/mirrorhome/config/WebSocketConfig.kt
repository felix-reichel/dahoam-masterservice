package at.htl.mirrorhome.config

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
open class WebSocketConfig: WebSocketMessageBrokerConfigurer {
	companion object {
		val MESSAGE_PREFIX = "/topic"
	}

	@Override override fun configureMessageBroker(config: MessageBrokerRegistry) {
		config!!.enableSimpleBroker(MESSAGE_PREFIX)
		config.setApplicationDestinationPrefixes("/app")
	}

	@Override override fun registerStompEndpoints(registry: StompEndpointRegistry) {
		registry!!.addEndpoint("/ws").setAllowedOrigins("*")
		registry!!.addEndpoint("/ws").setAllowedOrigins("*").withSockJS()
	}


}
