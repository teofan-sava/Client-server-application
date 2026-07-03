import oshi.SystemInfo
import java.io.PrintWriter
import java.util.concurrent.TimeUnit

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
