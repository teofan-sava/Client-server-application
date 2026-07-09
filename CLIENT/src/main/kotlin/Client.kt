import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Error: Port missing.")
        println("Usage: ./gradlew run --args=\"<PORT> [COMMAND] [ADDITIONAL_ARGUMENTS]\"")
        exitProcess(1)
    }

    val port = args[0].toIntOrNull()
    if (port == null) {
        println("Error: '${args[0]}' is not a valid port number.")
        exitProcess(1)
    }

    if (args.size == 1) {
        println("Connecting to server on port $port in interactive mode...")
        runInteractiveMode(port)
    } else {
        val command = args[1].uppercase()
        val additionalArgs = args.drop(2)
        runScriptableMode(port, command, additionalArgs)
    }
}
