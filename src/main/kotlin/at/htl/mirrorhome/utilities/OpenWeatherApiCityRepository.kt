package at.htl.mirrorhome.utilities

import org.springframework.data.repository.CrudRepository

interface OpenWeatherApiCityRepository: CrudRepository<OpenWeatherApiCity, Long> {
}