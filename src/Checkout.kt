
/**
 * Modalidades de entrega disponibles.
 */
enum class ModalidadEntrega {
    RECOGER_EN_TIENDA,
    A_DOMICILIO
}

/**
 * Representa una dirección previamente guardada.
 */
data class DireccionGuardada(
    val id: Int,
    val descripcion: String
) {
    init {
        require(id > 0) {
            "El ID de la dirección debe ser mayor que cero."
        }

        require(descripcion.isNotBlank()) {
            "La dirección no puede estar vacía."
        }
    }
}

/**
 * Opciones de pago disponibles en el checkout.
 */
enum class TipoPagoCheckout {
    EFECTIVO_EN_TIENDA,
    TARJETA
}

/**
 * Resumen del pedido antes de confirmarlo.
 */
data class ResumenCheckout(
    val items: List<ItemPedido>,
    val modalidadEntrega: ModalidadEntrega,
    val direccion: DireccionGuardada?,
    val tipoPago: TipoPagoCheckout,
    val metodoPago: MetodoPago?,
    val total: Double
)

/**
 * Gestiona las validaciones y el resumen del checkout.
 *
 * No utiliza pantallas ni Firestore.
 * No crea pedidos ni modifica el carrito.
 */
class CheckoutService {

    fun crearResumen(
        items: List<ItemPedido>,
        modalidadEntrega: ModalidadEntrega,
        direccionSeleccionada: DireccionGuardada?,
        tipoPago: TipoPagoCheckout,
        tarjetaId: Int?,
        metodosPagoDisponibles: List<MetodoPago>
    ): ResumenCheckout {

        require(items.isNotEmpty()) {
            "El carrito está vacío."
        }

        require(items.all { it.cantidad > 0 }) {
            "Todas las cantidades deben ser mayores que cero."
        }

        require(items.all { it.producto.precio >= 0.0 }) {
            "El precio de los productos no puede ser negativo."
        }

        // Validar la modalidad de entrega.
        when (modalidadEntrega) {
            ModalidadEntrega.RECOGER_EN_TIENDA -> {
                // No se necesita dirección para recoger en tienda.
            }

            ModalidadEntrega.A_DOMICILIO -> {
                require(direccionSeleccionada != null) {
                    "Debe seleccionar una dirección guardada."
                }
            }
        }

        // Validar el método de pago.
        val metodoPagoSeleccionado: MetodoPago? =
            when (tipoPago) {
                TipoPagoCheckout.EFECTIVO_EN_TIENDA -> {
                    require(tarjetaId == null) {
                        "El pago en efectivo no requiere una tarjeta."
                    }
                    null
                }

                TipoPagoCheckout.TARJETA -> {
                    require(!metodosPagoDisponibles.isEmpty()) {
                        "No hay tarjetas guardadas. " +
                                "Seleccione efectivo en tienda."
                    }

                    require(tarjetaId != null) {
                        "Debe seleccionar una tarjeta."
                    }

                    metodosPagoDisponibles.find {
                        it.id == tarjetaId
                    } ?: throw IllegalArgumentException(
                        "La tarjeta seleccionada no existe."
                    )
                }
            }

        // Copiar la lista para preparar el resumen.
        val itemsResumen = items.toList()

        val total = itemsResumen.sumOf {
            it.calcularSubtotal()
        }

        return ResumenCheckout(
            items = itemsResumen,
            modalidadEntrega = modalidadEntrega,
            direccion = if (
                modalidadEntrega == ModalidadEntrega.A_DOMICILIO
            ) direccionSeleccionada else null,
            tipoPago = tipoPago,
            metodoPago = metodoPagoSeleccionado,
            total = total
        )
    }
}
