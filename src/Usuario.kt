// ====================================================================
// USUARIO.kt
// ====================================================================

enum class Rol {
    ADMIN, EMPLEADO, CLIENTE
}

/**
 * Indica qué roles pueden modificar el inventario.
 * ADMIN y EMPLEADO tienen permiso; CLIENTE no.
 */
fun Rol.puedeModificarInventario(): Boolean {
    return this == Rol.ADMIN || this == Rol.EMPLEADO
}

data class Usuario(
    val nombre: String,
    val contrasena: String,
    val rol: Rol
)

class GestorUsuarios {

    // Lista de usuarios registrados
    private val usuarios = mutableListOf<Usuario>()

    fun registrarUsuario(nombre: String, contrasena: String, rol: Rol) {
        try {
            require(nombre.isNotBlank()) { "El nombre no puede estar vacío" }
            require(contrasena.length >= 4) { "La contraseña debe tener al menos 4 caracteres" }
            require(usuarios.none { it.nombre == nombre }) { "Ese nombre de usuario ya existe" }

            usuarios.add(Usuario(nombre, contrasena, rol))
            println("Usuario registrado: $nombre ($rol)")
        } catch (e: Exception) {
            Logger.registrarError("Usuario", "Error al registrar usuario: ${e.message}")
            println("Error: ${e.message}")
        }
    }
    /**
     * Registra un usuario como CLIENTE.
     */
    fun registrarUsuario(nombre: String, contrasena: String) {
        registrarUsuario(nombre, contrasena, Rol.CLIENTE)
    }

    fun iniciarSesion(nombre: String, contrasena: String): Usuario? {
        return try {
            val usuario = usuarios.find { it.nombre == nombre && it.contrasena == contrasena }
                ?: throw SecurityException("Usuario o contraseña incorrectos")
            println("Bienvenido, ${usuario.nombre} (${usuario.rol})")
            usuario
        } catch (e: Exception) {
            Logger.registrarError("Usuario", "Error de inicio de sesión: ${e.message}")
            println("Error: ${e.message}")
            null
        }
    }
}