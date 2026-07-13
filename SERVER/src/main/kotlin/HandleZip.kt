import java.io.DataInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipInputStream

fun handleZip(dataIn: DataInputStream, fileSize: Long, output: PrintWriter) {
    val tempDirectory = Files.createTempDirectory("compile_")
    val zipFile = File(tempDirectory.toFile(), "uploaded.zip")

    try {
        FileOutputStream(zipFile).use { fos ->
            val buffer = ByteArray(4096)
            var bytesLeft = fileSize
            while (bytesLeft > 0) {
                val toRead = minOf(buffer.size.toLong(), bytesLeft).toInt()
                val read = dataIn.read(buffer, 0, toRead)
                if (read == -1) throw java.io.EOFException("Network disconnected while uploading ZIP")
                fos.write(buffer, 0, read)
                bytesLeft -= read
            }
        }

        unzip(zipFile, tempDirectory)

        val workingDir = tempDirectory.toFile()

        val compileProcess = ProcessBuilder("sh", "-c", "gcc *.c -o executable")
            .directory(workingDir)
            .redirectErrorStream(true)   // merge stderr into stdout
            .start()

        val compileExit = compileProcess.waitFor()

        if (compileExit == 0) {
            val runProcess = ProcessBuilder("./executable")
                .directory(workingDir)
                .redirectErrorStream(true)
                .start()
            runProcess.waitFor()

            val execOutput = runProcess.inputStream.bufferedReader().readText()

            output.println("\n--- COMPILATION SUCCESSFUL ---")
            output.println("  --- EXECUTION OUTPUT ---")
            output.println(execOutput.ifBlank { "No output generated" })
        } else {
            val errorOutput = compileProcess.inputStream.bufferedReader().readText()
            output.println("--- COMPILATION UNSUCCESSFUL ---")
            output.println(errorOutput)
        }
    } catch (e: Exception) {
        output.println("ERROR: Server encountered an issue handling the ZIP data pipeline.")
        println("ERROR: ${e.message}")
    } finally {
        tempDirectory.toFile().deleteRecursively()
    }
}

fun unzip(zipFile: File, tempDirectory: Path) {
    ZipInputStream(zipFile.inputStream()).use { zis ->
        var entry = zis.nextEntry
        while (entry != null) {
            if (!entry.isDirectory) {
                val newFile = File(tempDirectory.toFile(), File(entry.name).name)
                FileOutputStream(newFile).use { fos ->
                    zis.copyTo(fos)
                }
            }
            zis.closeEntry()
            entry = zis.nextEntry
        }
    }
}
