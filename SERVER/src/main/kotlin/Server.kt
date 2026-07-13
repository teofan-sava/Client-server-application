import java.net.ServerSocket
import kotlin.concurrent.thread
import java.io.IOException

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Error: Provide a port number")
        println("Usage: ./gradlew run --args=\"8080\"")
        return
    }

    val port = args[0].toIntOrNull()
    if (port == null || port !in 1..65535) {
        println("Invalid port number. Please enter a valid number between 1 and 65535")
        return
    }

    try {
        val serverSocket = ServerSocket(port)
        println("Server is using port $port")

        while (true) {
            val clientSocket = serverSocket.accept()
            thread (start = true) {
                handleClient(clientSocket)
            }
        }
    } catch (e: IOException) {
        println("Server exception: ${e.message}")
    }
}
