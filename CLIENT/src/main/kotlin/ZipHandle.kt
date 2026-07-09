import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

fun sendZipPayload(port: Int, zipFilePath: String) {
    val file = File(zipFilePath)
    if (!file.exists() || !file.isFile) {
        println("File Error: Direct path absolute target or archive context not found: ${file.absolutePath}\n")
        return
    }

    println("Preparing to upload archive: ${file.name} (${file.length()} bytes)")

    try {
        Socket("localhost", port).use { socket ->
            val textOut = PrintWriter(socket.getOutputStream(), true)
            val rawBinaryOut = socket.getOutputStream()

            textOut.println("COMPILE_ZIP")
            textOut.println(file.length())

            FileInputStream(file).use { fis ->
                val buffer = ByteArray(4096)
                var bytesRead: Int
                while (fis.read(buffer).also { bytesRead = it } != -1) {
                    rawBinaryOut.write(buffer, 0, bytesRead)
                }
            }
            rawBinaryOut.flush()

            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            while (true) {
                val line = reader.readLine() ?: break
                if (line == "END") break
                println(line)
            }
        }
    } catch (e: Exception) {
        println("Network Pipeline Upload Exception: ${e.message}")
    }
}