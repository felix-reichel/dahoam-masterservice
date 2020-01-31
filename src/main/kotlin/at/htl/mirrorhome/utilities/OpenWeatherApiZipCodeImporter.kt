package at.htl.mirrorhome.utilities

import at.htl.mirrorhome.ApplicationContextHolder
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser
import org.slf4j.LoggerFactory
import java.io.FileReader
import java.time.LocalDateTime

open class OpenWeatherApiZipCodeImporter {
    companion object {
        val parser: JSONParser = JSONParser()
        val resourceFileName = "/usr/local/bin/city.list.json" // src/main/resources/
        //val filePath: String = this.javaClass.classLoader.getResource(resourceFileName).path


        private val log = LoggerFactory.getLogger(OpenWeatherApiZipCodeImporter::class.java)
        var openWeatherApiCityRepository: OpenWeatherApiCityRepository = ApplicationContextHolder.getContext().getBean(OpenWeatherApiCityRepository::class.java)

        fun import() {
            var obj: Any = parser.parse(FileReader(resourceFileName))
            var cities: JSONArray = obj as JSONArray

            cities.stream().forEach { c ->
                var city: JSONObject = c as JSONObject
                var id: Long = city.get("id") as Long
                var name: String = city.get("name") as String
                var countryIso = city.get("country") as String

                var longitude: Double = 0.0
                var latitude: Double = 0.0

                var lon: Any? = (city.get("coord") as JSONObject).get("lon")
                when (lon) {
                    is Int -> longitude = lon.toDouble()
                    is Double -> longitude = lon
                    else -> longitude = -1.0
                }
                var lat: Any? = (city.get("coord") as JSONObject).get("lat")
                when (lat) {
                    is Int -> latitude = lat.toDouble()
                    is Double -> latitude = lat
                    else -> latitude = -1.0
                }

                if (countryIso == "AT") { // performance
                    openWeatherApiCityRepository.save(OpenWeatherApiCity(-1L, -1L, id, name, countryIso, longitude, latitude))
                }

            }

            log.info("${openWeatherApiCityRepository.count()} [AT] cities imported by OpenWeatherApiZipCodeImporter @${LocalDateTime.now()}")

        }
    }

}