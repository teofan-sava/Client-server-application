import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.io.File
import java.net.URLEncoder

@Serializable
data class Current(val temperature_2m: Double, val weather_code: Int)

@Serializable
data class Weather(val latitude: Double, val longitude: Double, val current: Current)

fun weatherInfo(client: HttpClient, lat: String, long: String): String {

    return try {
        val url = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$long&current=temperature_2m,weather_code"
        val request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        if (response.statusCode() != 200) {
            println("Failed to fetch the weather (Status = ${response.statusCode()})\n")
            return "Incorrect latitude and longitude. Latitude: -90° to +90°. Longitude: -180° to +180°\n"
        }

        val jsonConfig = Json {
            ignoreUnknownKeys = true
        }

        val body = response.body()
        val data = jsonConfig.decodeFromString<Weather>(body)
        val temp = data.current.temperature_2m
        val code = data.current.weather_code

        "Temperature: $temp °C | Condition Code: $code\n"
    } catch (e: Exception) {
        "Error retrieving weather data: ${e.message}"
    }
}

@Serializable
data class City(val lat: String, val lon: String)

fun weatherInfo(client: HttpClient, city: String): String {
    val file = File("API_KEY.txt")
    return try {
        val key = file.bufferedReader().use { it.readLine() }
        val cityName = city.trim().removeSurrounding("\"")
        if (cityName.isBlank()) return "Incorrect city name.\n"

        val encodedCity = URLEncoder.encode(cityName, "UTF-8")
        val addressUrl = "https://geocode.maps.co/search?q=$encodedCity&api_key=$key"

        val request = HttpRequest.newBuilder().uri(URI.create(addressUrl)).GET().build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        if (response.statusCode() != 200) {
            println("Failed to fetch the weather (Status = ${response.statusCode()})\n")
            return "Incorrect city name.\n"
        }

        val jsonConfig = Json { ignoreUnknownKeys = true }
        val data: List<City> = jsonConfig.decodeFromString(response.body())
        val lat = data[0].lat
        val long = data[0].lon
        weatherInfo(client, lat, long)
    } catch (e: Exception) {
        if (e is java.io.FileNotFoundException)
            "Error: API_KEY.txt not found. Please place your geocode.maps.co API key in that file."
        else
            "Error retrieving weather data: ${e.message}"
    }
}