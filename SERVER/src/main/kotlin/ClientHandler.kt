import java.io.DataInputStream
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
        val dataIn = DataInputStream(socket.getInputStream())
        val output = PrintWriter(socket.getOutputStream(), true)

        while (true) {
            val rawLine = readLine(dataIn) ?: break
            if (rawLine.isBlank()) continue

            val parts = rawLine.trim().split(Regex("\\s+"))
            val command = parts[0].uppercase()

            println("[Client $clientId] Requested command: $command")

            if (command == "EXIT") {
                break
            }

            when (command) {
                "TIME" -> {
                    output.println("Current server date and time: ${java.time.LocalDate.now()}, ${java.time.LocalTime.now().format(timeFormatter)}")
                }

                "OS" -> osInfo(output)

                "WEATHER" -> {
                    val args = rawLine.substringAfter(' ').trim()
                    handleWeatherInput(args, clientId, httpClient, output)
                }

                "COMPILE_ZIP" -> {
                    val sizeLine = readLine(dataIn) ?: break
                    val fileSize = sizeLine.toLongOrNull()

                    if (fileSize != null && fileSize > 0) {
                        handleZip(dataIn, fileSize, output)
                    } else {
                        output.println("ERROR: Malformed file size parameter payload.")
                    }
                }

                else -> {
                    output.println("ERROR: Unknown API command $command")
                }
            }

            output.println("END")
            output.flush()
        }

    } catch (e: IOException) {
        println("[Client $clientId] Connection error: ${e.message}")
    } finally {
        socket.close()
        println("[Client $clientId] Connection closed")
    }
}

fun readLine(dataIn: DataInputStream): String? {
    val sb = StringBuilder()
    while (true) {
        val b = dataIn.read()
        if (b == -1) {
            if (sb.isEmpty()) return null // Stream închis complet
            break
        }
        if (b == '\n'.code) break
        if (b != '\r'.code) {
            sb.append(b.toChar())
        }
    }
    return sb.toString()
}
