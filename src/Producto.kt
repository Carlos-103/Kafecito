// ====================================================================
// PRODUCTO.kt  -- Módulo de MENÚ
// Responsable:
// ====================================================================
// Requerimiento funcional que cubre:
//   1. Módulo de gestión principal (crear, listar, actualizar, eliminar)
//
// Qué tienes que hacer:
//   1. Completar la clase Producto con sus atributos.
//   2. Implementar las funciones CRUD dentro de GestorProductos.
//   3. Usar Logger.registrarError(...) dentro de cada try/catch.

data class Producto(
    val id: Int,
    var nombre: String,
    var precio: Double,
    var categoria: String,
    var disponible: Boolean = true
)

class GestorProductos {

    // Manejo de colecciones: aquí se guardan todos los productos
    private val productos = mutableListOf<Producto>()
    private var siguienteId = 1

    fun agregarProducto(nombre: String, precio: Double, categoria: String) {
        try {
            require(nombre.isNotBlank()) { "El nombre no puede estar vacío" }
            require(precio > 0) { "El precio debe ser mayor a 0" }

            val nuevo = Producto(siguienteId, nombre, precio, categoria)
            productos.add(nuevo)
            siguienteId++
            println("Producto agregado: $nuevo")
        } catch (e: Exception) {
            Logger.registrarError("Producto", "Error al agregar producto: ${e.message}")
            println("Error: ${e.message}")
        }
    }

    fun listarProductos() {
        if (productos.isEmpty()) {
            println("No hay productos registrados.")
            return
        }
        productos.forEach { println(it) }
    }

    fun actualizarProducto(id: Int, nuevoPrecio: Double) {
        try {
            val producto = productos.find { it.id == id }
                ?: throw NoSuchElementException("No existe un producto con id $id")
            require(nuevoPrecio > 0) { "El precio debe ser mayor a 0" }
            producto.precio = nuevoPrecio
            println("Producto actualizado: $producto")
        } catch (e: Exception) {
            Logger.registrarError("Producto", "Error al actualizar producto: ${e.message}")
            println("Error: ${e.message}")
        }
    }

    fun eliminarProducto(id: Int) {
        try {
            val eliminado = productos.removeIf { it.id == id }
            if (!eliminado) throw NoSuchElementException("No existe un producto con id $id")
            println("Producto eliminado correctamente.")
        } catch (e: Exception) {
            Logger.registrarError("Producto", "Error al eliminar producto: ${e.message}")
            println("Error: ${e.message}")
        }
    }

    // Útil para que el módulo de Reportes (José Antonio) pueda leer los datos
    fun obtenerProductos(): List<Producto> = productos
}
