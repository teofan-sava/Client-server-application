import java.io.PrintWriter

fun sendMenu(output: PrintWriter) {
    output.println("\t=== SERVER API MENU ===")
    output.println("SUPPORTED COMMANDS:")
    output.println("  HELP                  Display the menu again")
    output.println("  TIME                  Display the server's current time")
    output.println("  OS                    Display the server's OS details")
    output.println("  WEATHER <lat> <long>  Display the current weather")
    output.println("  EXIT                  Close the connection")
    output.println("-------------------------------------------------------------\n")
}
