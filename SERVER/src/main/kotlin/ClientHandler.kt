import oshi.SystemInfo
import java.io.IOException
import java.io.PrintWriter
import java.net.Socket
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

fun handleClient(socket: Socket) {

    val clientId = socket.remoteSocketAddress.toString()
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
                    output.println("Current server date and time: ${java.time.LocalDate.now()}, ${java.time.LocalTime.now().format(timeFormatter)}\n")
                }
                "OS" -> osInfo(output)
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

fun osInfo(output: PrintWriter) {

    val si = SystemInfo()
    val os = si.operatingSystem
    val hardware = si.hardware
    val processor = hardware.processor
    val memory = hardware.memory
    val uptimeSeconds = os.systemUptime

    output.println("=== OPERATING SYSTEM INFORMATION ===")
    output.println("OS Family: ${os.family}")
    output.println("Manufacturer: ${os.manufacturer}")
    output.println("Version :${os.versionInfo}")

    val days = TimeUnit.SECONDS.toDays(uptimeSeconds)
    val hours = TimeUnit.SECONDS.toHours(uptimeSeconds) % 24
    val minutes = TimeUnit.SECONDS.toMinutes(uptimeSeconds) % 60
    output.println("System uptime: $days days, $hours hours, $minutes minutes")

    val availableMemGB = memory.available.toDouble() / (1024 * 1024 * 1024)
    val totalMemGB = memory.total.toDouble() / (1024 * 1024 * 1024)
    output.printf("Available Memory: %.2f GB / %.2f GB\n", availableMemGB, totalMemGB)

    output.println("CPU type: ${processor.processorIdentifier.name}")
    output.println("CPU Physical Cores: ${processor.physicalProcessorCount}")
    output.println("CPU Logical Cores: ${processor.logicalProcessorCount}\n")
}

fun sendMenu(output: PrintWriter) {
    output.println("\t=== SERVER API MENU ===")
    output.println("SUPPORTED COMMANDS:")
    output.println("  HELP      Display the menu again")
    output.println("  TIME      Display the server's current time")
    output.println("  OS        Display the server's OS details")
    output.println("  EXIT      Close the connection")
    output.println("-----------------------------------------------\n")
}
