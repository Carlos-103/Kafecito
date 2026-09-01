// ====================================================================
// PEDIDO.kt  -- Módulo de PEDIDOS
// Responsable:
// ====================================================================
// Requerimiento funcional que cubre:
//   2. Módulo de procesamiento o cálculo (lógica de negocio central)
//
// Qué tienes que hacer:
//   1. Completar la clase Pedido y su lista de productos.
//   2. Implementar el cálculo del total.
//   3. Implementar el cambio de estado (en preparación -> listo -> entregado).

enum class EstadoPedido {
    EN_PREPARACION, LISTO, ENTREGADO
}

data class DetallePedido(
    val producto: Producto,
    val cantidad: Int
)

data class Pedido(
    val id: Int,
    val cliente: String,
    val detalles: MutableList<DetallePedido> = mutableListOf(),
    var estado: EstadoPedido = EstadoPedido.EN_PREPARACION
) {
    //Calcula el detalle del peido
    fun calcularTotal(): Double {
        return try {
            detalles.sumOf{it.producto.precio*it.cantidad}
        }catch (e: Exception){
            Logger.registrarError("Pedido", "Erro al calcular detalle pedido")
        }
    }
    //Agregar producto y su cantidad al pedido actual
    fun agregarProducto(){
        try{
            require(cantidad>0){"La cantidad debe ser mayor a 0"}
            detalles.add(DetallePedido(producto,cantidad))
            println("Producto '${producto.nombre}'(x$cantidad) agregado al pedido #$id")
        }catch (e:Exception){
            Logger.registrarError("Pedido","Error al agregar al pedido #$id: ${e.message}")
            println("Error: ${e.message}")
        }
    }
}

class GestorPedidos {

    // Manejo de colecciones: todos los pedidos registrados
    private val pedidos = mutableListOf<Pedido>()
    private var siguienteId = 1

    fun crearPedido(cliente: String, items: List<ItemPedido>): Pedido? {
        return try {
            require(cliente.isNotBlank()) { "El cliente no puede estar vacío" }
            require(items.isNotEmpty()) { "El pedido debe tener al menos un producto" }

            val nuevo = Pedido(siguienteId, cliente, items.toMutableList())
            pedidos.add(nuevo)
            siguienteId++
            println("Pedido creado: #${nuevo.id} - Total: $${nuevo.calcularTotal()}")
            nuevo
        } catch (e: Exception) {
            Logger.registrarError("Pedido", "Error al crear pedido: ${e.message}")
            println("Error: ${e.message}")
            null
        }
    }

    fun actualizarEstado(idPedido: Int, nuevoEstado: EstadoPedido) {
        try {
            val pedido = pedidos.find { it.id == idPedido }
                ?: throw NoSuchElementException("No existe el pedido $idPedido")
            pedido.estado = nuevoEstado
            println("Pedido #$idPedido actualizado a estado: $nuevoEstado")
        } catch (e: Exception) {
            Logger.registrarError("Pedido", "Error al actualizar estado: ${e.message}")
            println("Error: ${e.message}")
        }
    }

    fun consultarEstado(idPedido: Int): EstadoPedido? =
        pedidos.find { it.id == idPedido }?.estado

    // Útil para que el módulo de Reportes (José Antonio) pueda leer los datos
    fun obtenerPedidos(): List<Pedido> = pedidos
}
