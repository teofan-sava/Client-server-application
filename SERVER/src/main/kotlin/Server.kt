import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
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
            println("New user connected from: ${clientSocket.remoteSocketAddress}")

            thread (start = true) {
                handleClient(clientSocket)
            }
        }
    } catch (e: IOException) {
        println("Server exception: ${e.message}")
    }
}

fun handleClient(socket: Socket) {
    try {
        val input = socket.getInputStream().bufferedReader()
        val output = PrintWriter(socket.getOutputStream(), true)

        output.println("Welcome to the server! Type 'exit' to disconnect.")

        var clientMessage: String?
        while (input.readLine().also {clientMessage = it} != null) {
            println("[Client: ${socket.remoteSocketAddress}]: $clientMessage")

            if (clientMessage?.trim()?.lowercase() == "exit") {
                output.println("Goodbye!")
                return
            }
            output.println("Server echoed: $clientMessage")
        }
    } catch (e: IOException) {
        println("Exception while handling client ${socket.remoteSocketAddress}: ${e.message}")
    } finally {
        try {
            socket.close()
            println("Client disconnected: ${socket.remoteSocketAddress}")
        } catch (e: IOException) {
            println("Failed to close socket: ${e.message}")
        }
    }
}
