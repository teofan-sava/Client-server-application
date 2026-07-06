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
            val parts = rawLine.trim().split(Regex("\\s+"), limit = 2)
            val command = parts[0].uppercase()
            val argument = if (parts.size > 1) parts[1] else ""

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

                "WEATHER" -> {
                    val regex = """("[^"]*")|\S+""".toRegex()
                    val parts = regex.findAll(rawLine).map { it.value }.toList()
                    handleWeatherInput(parts, clientId, httpClient, output)
                }

                "ZIP" -> {
                    if (argument.isNotBlank()) {
                        handleZip(argument, output)
                    } else {
                        output.println("You must provide a path")
                    }
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
