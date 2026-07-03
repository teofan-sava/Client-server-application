import java.io.IOException
import java.io.PrintWriter
import java.net.Socket
import java.time.format.DateTimeFormatter
import java.net.http.HttpClient

fun handleClient(socket: Socket) {

    val clientId = socket.remoteSocketAddress.toString()
    val httpClient = HttpClient.newHttpClient()
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

    println("[Client $clientId] connected to the server")

    try {
        val input = socket.getInputStream().bufferedReader()
        val output = PrintWriter(socket.getOutputStream(), true)

        sendMenu(output)

        while (true) {
            val rawLine = input.readLine() ?: break
            val command = rawLine.trim().uppercase()
            println("[Client $clientId] Requested command: $command")

            when (command) {
                "HELP" -> sendMenu(output)

                "TIME" -> {
                    output.println(
                        "Current server date and time: ${java.time.LocalDate.now()}, ${
                            java.time.LocalTime.now().format(timeFormatter)
                        }\n"
                    )
                }

                "OS" -> osInfo(output)

                command.takeIf { it.startsWith("WEATHER") } -> {
                    val parts = rawLine.trim().split("\\s+".toRegex())
                    if (parts.size < 3) {
                        output.println("Missing parameters. Format: WEATHER <lat> <long>\n")
                        continue
                    }

                    val lat = parts[1]
                    val long = parts[2]
                    println("[Client $clientId] Requested weather for Lat: $lat, Long: $long")

                    val weatherInfo = weatherInfo(httpClient, lat, long)
                    output.println(weatherInfo)
                }

                "EXIT" -> {
                    output.println("Disconnecting. Goodbye!")
                    break
                }

                else -> {
                    output.println("Unknown command $rawLine. Type 'HELP' for menu\n")
                }
            }
        }
    } catch (e: IOException) {
        println("Connection error: ${e.message}")
    } finally {
        socket.close()
        println("[Client $clientId] Connection closed")
    }
}
