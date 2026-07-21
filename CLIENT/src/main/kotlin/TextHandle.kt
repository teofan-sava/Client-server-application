import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

fun sendTextCommand(port: Int, payload: String) {

    val host = System.getenv("SERVER_HOST") ?: "localhost"

    try {
        Socket(host, port).use { socket ->
            val out = PrintWriter(socket.getOutputStream(), true)
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

            out.println(payload)

            while (true) {
                val line = reader.readLine() ?: break
                if (line == "END") break
                println(line)
            }
        }
    } catch (e: Exception) {
        println("Network Error: Could not connect or communicate with server -> ${e.message}")
    }
}
