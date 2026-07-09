import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

fun runInteractiveMode(port: Int) {
    menuDisplay()

    try {
        Socket("localhost", port).use { socket ->
            val out = PrintWriter(socket.getOutputStream(), true)
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            val rawBinaryOut = socket.getOutputStream()

            while (true) {
                println()
                print("Enter command: ")
                val inputLine = readlnOrNull()?.trim() ?: break
                if (inputLine.isEmpty()) continue

                val parts = inputLine.split(Regex("\\s+"), limit = 2)
                val choice = parts[0].uppercase()
                val argument = if (parts.size > 1) parts[1] else ""

                when (choice) {
                    "1", "TIME" -> {
                        out.println("TIME")
                        readServerResponse(reader)
                    }
                    "2", "OS" -> {
                        out.println("OS")
                        readServerResponse(reader)
                    }
                    "3", "WEATHER" -> {
                        if (argument.isBlank()) {
                            println("Error: Missing location.")
                            continue
                        }
                        out.println("WEATHER $argument")
                        readServerResponse(reader)
                    }
                    "4", "ZIP" -> {
                        if (argument.isBlank()) {
                            println("Error: Missing file path.")
                            continue
                        }
                        sendZipFile(argument, out, rawBinaryOut, reader)
                    }
                    "5", "EXIT" -> {
                        out.println("EXIT")
                        println("Closing connection. Goodbye!")
                        break
                    }
                    "6", "HELP" -> menuDisplay()
                    else -> println("Unknown command. Type HELP to see options.")
                }
            }
        }
    } catch (e: Exception) {
        println("Connection error: ${e.message}")
    }
}

fun readServerResponse(reader: BufferedReader) {
    while (true) {
        val line = reader.readLine() ?: break
        if (line == "END") break
        println(line)
    }
}

fun sendZipFile(zipFilePath: String, out: PrintWriter, rawBinaryOut: java.io.OutputStream, reader: BufferedReader) {
    val file = java.io.File(zipFilePath)
    if (!file.exists() || !file.isFile) {
        println("File Error: Archive not found: ${file.absolutePath}\n")
        return
    }

    out.println("COMPILE_ZIP")
    out.println(file.length())
    out.flush()

    java.io.FileInputStream(file).use { fis ->
        val buffer = ByteArray(4096)
        var bytesRead: Int
        while (fis.read(buffer).also { bytesRead = it } != -1) {
            rawBinaryOut.write(buffer, 0, bytesRead)
        }
    }
    rawBinaryOut.flush()

    readServerResponse(reader)
}
