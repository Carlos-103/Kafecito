// ====================================================================
// USUARIO.kt  -- Módulo de USUARIOS / LOGIN
// Responsable:
// ====================================================================
// Requerimiento técnico que cubre: Validación de entradas y roles
//
// Qué tienes que hacer:
//   1. Completar el registro y login de usuarios.
//   2. Validar que el rol sea uno de los 3 permitidos.
//   3. Validar que la contraseña no esté vacía / cumpla un mínimo.

enum class Rol {
    ADMIN, EMPLEADO, CLIENTE
}

data class Usuario(
    val nombre: String,
    val contrasena: String,
    val rol: Rol
)

class GestorUsuarios {

    // Manejo de colecciones: todos los usuarios registrados
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
