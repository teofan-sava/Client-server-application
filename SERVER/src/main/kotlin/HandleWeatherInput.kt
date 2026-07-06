import java.io.PrintWriter
import java.net.http.HttpClient

fun handleWeatherInput(parts: List<String>, clientId: String, httpClient: HttpClient, output: PrintWriter) {
    if (parts.size == 3) {

        if ((parts[1].toDoubleOrNull() != null) && (parts[2].toDoubleOrNull() != null)) {
            val lat = parts[1]
            val long = parts[2]
            println("[Client $clientId] Requested weather for Lat: $lat, Long: $long")

            val weatherInfo = weatherInfo(httpClient, lat, long)
            output.println(weatherInfo)
        } else {
            output.println("Latitude and longitude can only be numbers.\n")
        }
    } else if (parts.size == 2) {

        val city = parts[1]
        println("[Client $clientId] Requested weather for city: $city")

        val weatherInfo = weatherInfo(httpClient, city)
        output.println(weatherInfo)
    } else {
        output.println("Wrong parameters. Format: WEATHER <lat> <long> || WEATHER \"<city>\"\n")
    }
}
