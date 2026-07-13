fun runScriptableMode(port: Int, command: String, remainingArgs: List<String>) {
    val joinedArgs = remainingArgs.joinToString(" ")

    when (command) {
        "1", "TIME" -> sendTextCommand(port, "TIME")
        "2", "OS"   -> sendTextCommand(port, "OS")
        "3", "WEATHER" -> {
            if (joinedArgs.isBlank()) {
                println("Error: WEATHER command requires location arguments.")
                return
            }
            sendTextCommand(port, "WEATHER $joinedArgs")
        }
        "4", "ZIP" -> {
            if (joinedArgs.isBlank()) {
                println("Error: ZIP command requires a file path argument.")
                return
            }
            sendZipPayload(port, joinedArgs)
        }
        "5", "EXIT" -> sendTextCommand(port, "EXIT")
        else -> println("Error: Scriptable command unrecognized.")
    }
}
