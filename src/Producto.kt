data class Producto(
    val id: Int,
    var nombre: String,
    var precio: Double,
    var categoria: String,
    var disponible: Boolean = true
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
     * Lista solo los productos marcados como disponibles.
     */
    fun listarDisponibles() {
        val disponibles = productos.filter { it.disponible }
        if (disponibles.isEmpty()) {
            println("No hay productos disponibles en este momento.")
            return
        }
        println()
        println("========== PRODUCTOS DISPONIBLES ==========")
        disponibles.forEach { producto ->
            println("ID: ${producto.id} | ${producto.nombre} | $${"%.2f".format(producto.precio)} | ${producto.categoria}")
        }
    }

    /**
     * Lista productos filtrados por categoría.
     */
    fun listarPorCategoria(categoria: String) {
        val filtrados = productos.filter { it.categoria.equals(categoria, ignoreCase = true) }
        if (filtrados.isEmpty()) {
            println("No hay productos en la categoría '$categoria'.")
            return
        }
        println()
        println("========== CATEGORÍA: $categoria ==========")
        filtrados.forEach { producto ->
            val estado = if (producto.disponible) "Disponible" else "No disponible"
            println("ID: ${producto.id} | ${producto.nombre} | $${"%.2f".format(producto.precio)} | $estado")
        }
    }

    /**
     * Busca productos cuyo nombre contenga el texto dado.
     */
    fun buscarPorNombre(nombre: String): List<Producto> {
        val resultados = productos.filter { it.nombre.contains(nombre, ignoreCase = true) }
        if (resultados.isEmpty()) {
            println("No se encontraron productos con el nombre '$nombre'.")
        } else {
            println()
            println("========== RESULTADOS DE BÚSQUEDA ==========")
            resultados.forEach { producto ->
                println("ID: ${producto.id} | ${producto.nombre} | $${"%.2f".format(producto.precio)} | ${producto.categoria}")
            }
        }
        return resultados
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
     * Marca un producto como no disponible (agotado).
     */
    fun marcarNoDisponible(id: Int) {
        try {
            val producto = productos.find { it.id == id }
                ?: throw NoSuchElementException("No existe un producto con id $id")
            producto.disponible = false
            println("⚠ '${producto.nombre}' marcado como AGOTADO / NO DISPONIBLE.")
        } catch (e: Exception) {
            Logger.registrarError("Producto", "Error al marcar no disponible: ${e.message}")
            println("Error: ${e.message}")
        }
    }

    /**
     * Marca un producto como disponible.
     */
    fun marcarDisponible(id: Int) {
        try {
            val producto = productos.find { it.id == id }
                ?: throw NoSuchElementException("No existe un producto con id $id")
            producto.disponible = true
            println("✅ '${producto.nombre}' marcado como DISPONIBLE.")
        } catch (e: Exception) {
            Logger.registrarError("Producto", "Error al marcar disponible: ${e.message}")
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
