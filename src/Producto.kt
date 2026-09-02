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
    var categoria: String
)

class GestorProductos {

    private val productos =
        mutableListOf<Producto>()

    private var siguienteId = 1

    /**
     * Agrega un nuevo producto.
     */
    fun agregarProducto(
        nombre: String,
        precio: Double,
        categoria: String
    ): Producto? {

        return try {

            require(nombre.isNotBlank()) {
                "El nombre no puede estar vacío"
            }

            require(precio > 0) {
                "El precio debe ser mayor que 0"
            }

            require(categoria.isNotBlank()) {
                "La categoría no puede estar vacía"
            }

            val nuevo = Producto(
                id = siguienteId,
                nombre = nombre,
                precio = precio,
                categoria = categoria
            )

            productos.add(nuevo)

            siguienteId++

            println()
            println("✅ Producto agregado correctamente.")
            println("ID: ${nuevo.id}")
            println("Nombre: ${nuevo.nombre}")
            println("Precio: $${"%.2f".format(nuevo.precio)}")
            println("Categoría: ${nuevo.categoria}")

            nuevo

        } catch (e: Exception) {

            Logger.registrarError(
                "Producto",
                "Error al agregar producto: ${e.message}"
            )

            println("Error: ${e.message}")

            null
        }
    }

    /**
     * Lista productos sin mostrar inventario.
     */
    fun listarProductos() {

        if (productos.isEmpty()) {

            println(
                "No hay productos registrados."
            )

            return
        }

        println()
        println("========== PRODUCTOS ==========")

        productos.forEach { producto ->

            println(
                "ID: ${producto.id} | " +
                        "${producto.nombre} | " +
                        "$${"%.2f".format(producto.precio)} | " +
                        "${producto.categoria}"
            )
        }

        println("===============================")
    }

    /**
     * Lista productos mostrando stock.
     */
    fun listarProductos(
        inventario: GestorInventario
    ) {

        if (productos.isEmpty()) {

            println(
                "No hay productos registrados."
            )

            return
        }

        println()
        println("========== MENÚ KAFECITO ==========")

        productos.forEach { producto ->

            val stock =
                inventario.consultarStock(producto.id)

            println("-----------------------------------")
            println("ID: ${producto.id}")
            println("Producto: ${producto.nombre}")
            println("Categoría: ${producto.categoria}")
            println(
                "Precio: $${"%.2f".format(producto.precio)}"
            )

            if (stock > 0) {

                println(
                    "Disponibilidad: $stock unidades"
                )

            } else {

                println(
                    "Disponibilidad: ❌ NO DISPONIBLE"
                )
            }
        }

        println("-----------------------------------")
    }

    /**
     * Actualiza el precio de un producto.
     */
    fun actualizarProducto(
        id: Int,
        nuevoPrecio: Double
    ) {

        try {

            val producto =
                productos.find { it.id == id }
                    ?: throw NoSuchElementException(
                        "No existe un producto con ID $id"
                    )

            require(nuevoPrecio > 0) {
                "El precio debe ser mayor que 0"
            }

            producto.precio = nuevoPrecio

            println(
                "✅ Producto actualizado:"
            )

            println(producto)

        } catch (e: Exception) {

            Logger.registrarError(
                "Producto",
                "Error al actualizar producto: ${e.message}"
            )

            println("Error: ${e.message}")
        }
    }

    /**
     * Elimina un producto.
     */
    fun eliminarProducto(
        id: Int
    ) {

        try {

            val eliminado =
                productos.removeIf {
                    it.id == id
                }

            if (!eliminado) {

                throw NoSuchElementException(
                    "No existe un producto con ID $id"
                )
            }

            println(
                "✅ Producto eliminado correctamente."
            )

        } catch (e: Exception) {

            Logger.registrarError(
                "Producto",
                "Error al eliminar producto: ${e.message}"
            )

            println("Error: ${e.message}")
        }
    }

    /**
     * Devuelve todos los productos.
     */
    fun obtenerProductos(): List<Producto> =
        productos
}
