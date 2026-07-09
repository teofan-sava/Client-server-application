import java.io.PrintWriter
import java.net.http.HttpClient

fun handleWeatherInput(args: String, clientId: String, httpClient: HttpClient, output: PrintWriter) {
    if (args.isBlank()) {
        output.println("Wrong parameters. Format: WEATHER <lat> <long> || WEATHER \"<city>\"\n")
        return
    }

    val tokens = args.split(Regex("\\s+"))
    if (tokens.size == 2) {
        val lat = tokens[0].toDoubleOrNull()
        val long = tokens[1].toDoubleOrNull()
        if (lat != null && long != null) {
            println("[Client $clientId] Requested weather for Lat: $lat, Long: $long")
            output.println(weatherInfo(httpClient, lat.toString(), long.toString()))
            return
        }
    }

    val city = args.trim().removeSurrounding("\"")
    println("[Client $clientId] Requested weather for city: $city")
    output.println(weatherInfo(httpClient, city))
}