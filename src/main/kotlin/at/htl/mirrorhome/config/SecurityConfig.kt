package at.htl.mirrorhome.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import java.util.Arrays

@Configuration
@EnableWebSecurity
open class SecurityConfig : WebSecurityConfigurerAdapter() {
	@Throws(Exception::class)
	override protected fun configure(http: HttpSecurity) {
		http.csrf().disable()
		http.authorizeRequests()
			.anyRequest().permitAll()
			.and()
			.formLogin().permitAll()
	}

	@Bean
	open fun corsConfigurationSource(): CorsConfigurationSource? {
		val configuration = CorsConfiguration()
		configuration.setAllowedOrigins(Arrays.asList("*"))
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"))
		configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "basic"))
		configuration.setExposedHeaders(Arrays.asList("basic"))
		val source = UrlBasedCorsConfigurationSource()
		source.registerCorsConfiguration("/**", configuration)
		return source
	}

}