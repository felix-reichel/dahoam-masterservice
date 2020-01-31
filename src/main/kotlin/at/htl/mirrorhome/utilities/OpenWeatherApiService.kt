package at.htl.mirrorhome.utilities

import at.htl.mirrorhome.user.User
import at.htl.mirrorhome.user.UserService
import org.springframework.stereotype.Service

@Service
class OpenWeatherApiService(
    val openWeatherApiCityRepository: OpenWeatherApiCityRepository,
    val userService: UserService
) {
    fun getAvailableCities(): List<OpenWeatherApiCity> {
        return this.openWeatherApiCityRepository.findAll().toList()
    }

    fun getOpenWeatherApiCityByName(cityName: String): OpenWeatherApiCity? {
        return this.openWeatherApiCityRepository.findAll()
                .filter { c -> c.name.toLowerCase() == cityName.toLowerCase() }.getOrNull(0)
    }

    fun getOpenWeatherApiCityByActiveUser(): OpenWeatherApiCity? {
        var activeUser: User? = this.userService.getActiveUserEntity()
        var userSettingsForecastLocation: Long? = activeUser?.userSettings?.cityZipForWeatherForecast
        return this.openWeatherApiCityRepository.findAll()
                .filter { c -> c.id == userSettingsForecastLocation}.getOrNull(0)
    }
}