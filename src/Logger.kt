// ====================================================================
// LOGGER.kt
// ====================================================================


import java.io.File
import java.time.LocalDateTime

object Logger {

    private const val RUTA_LOG = "errores.txt"

    fun registrarError(modulo: String, mensaje: String) {
        val linea = "[${LocalDateTime.now()}] [$modulo] $mensaje\n"
        try {
            File(RUTA_LOG).appendText(linea)
        } catch (e: Exception) {
            // Si no se puede guardar el log, se muestra en consola
            println("No se pudo escribir en el log: ${e.message}")
        }
    }
}
