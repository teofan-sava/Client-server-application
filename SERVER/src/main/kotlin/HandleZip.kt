import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipInputStream

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

fun handleZip(zipPath: String, output: PrintWriter) {
    val zipFile = File(zipPath)

    if (!zipFile.exists()) {
        output.println("ERROR: Nothing exists at $zipPath. Path should look like: '/home/name/path_to_zip_archive'.\n")
        return
    }

    if (!zipFile.isFile) {
        output.println("ERROR: The path exists, but it doesn't point to a .zip archive file.\n")
        return
    }

    output.println("Server received path. Processing...")
    val tempDirectory = Files.createTempDirectory("compile_")

    try {
        unzip(zipFile, tempDirectory)

        val compileProcess = ProcessBuilder("sh", "-c", "gcc *.c -o executable").directory(tempDirectory.toFile()).start()
        compileProcess.waitFor()

        if (compileProcess.exitValue() == 0) {
            val runProcess = ProcessBuilder("./executable").directory(tempDirectory.toFile()).start()
            runProcess.waitFor()
            val execOutput = runProcess.inputStream.bufferedReader().readText()

            output.println("--- COMPILATION SUCCESSFUL ---")
            output.println("  --- EXECUTION OUTPUT ---")
            output.println(execOutput.ifBlank { "No output generated\n" })
        } else {
            val errorLog = compileProcess.errorStream.bufferedReader().readText()
            output.println("--- COMPILATION UNSUCCESSFUL ---")
            output.println(errorLog)
        }
    } catch (e: Exception) {
        output.println("ERROR: Server encountered an issue")
        println("ERROR: ${e.message}")
    } finally {
        tempDirectory.toFile().deleteRecursively()
    }
}
