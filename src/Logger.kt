// ====================================================================
// LOGGER.kt  -- Utilidad COMPARTIDA (todos la usan, nadie la borra)
// Responsable: uso de TODOS / mantenimiento:
// ====================================================================
// Requerimiento técnico que cubre: "Log de errores en archivo de texto"
//
// Cómo se usa desde cualquier módulo:
//
//      try {
//          // código que puede fallar
//      } catch (e: Exception) {
//          Logger.registrarError("Inventario", "Stock inválido: ${e.message}")
//      }

import java.io.File
import java.time.LocalDateTime

object Logger {

    private const val RUTA_LOG = "errores.txt"

    fun registrarError(modulo: String, mensaje: String) {
        val linea = "[${LocalDateTime.now()}] [$modulo] $mensaje\n"
        try {
            File(RUTA_LOG).appendText(linea)
        } catch (e: Exception) {
            // Si ni siquiera se puede escribir el log, lo mostramos en consola
            println("No se pudo escribir en el log: ${e.message}")
        }
    }
}
