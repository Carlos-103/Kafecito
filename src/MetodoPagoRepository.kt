
/**
 * Repositorio simulado de métodos de pago.
 *
 * Los datos se almacenan en memoria mientras se ejecuta
 * el programa. No utiliza Firestore ni una base de datos.
 */
class MetodoPagoRepository {

    private val metodosPago = mutableListOf<MetodoPago>()
    private var siguienteId = 1

    /**
     * Devuelve todos los métodos de pago registrados.
     */
    fun listar(): List<MetodoPago> {
        return metodosPago.toList()
    }

    /**
     * Registra una tarjeta utilizando solamente su marca
     * y sus últimos cuatro dígitos.
     */
    fun agregar(
        marca: String,
        ultimosCuatroDigitos: String
    ): MetodoPago {
        val nuevoMetodo = MetodoPago(
            id = siguienteId,
            marca = marca.trim(),
            ultimosCuatroDigitos = ultimosCuatroDigitos
        )

        metodosPago.add(nuevoMetodo)
        siguienteId++

        return nuevoMetodo
    }

    /**
     * Elimina un método de pago según su identificador.
     *
     * Devuelve true si se eliminó y false si no existía.
     */
    fun borrar(id: Int): Boolean {
        return metodosPago.removeAll { it.id == id }
    }

    /**
     * Busca un método de pago por su identificador.
     */
    fun buscarPorId(id: Int): MetodoPago? {
        return metodosPago.find { it.id == id }
    }

    /**
     * Indica si no hay métodos de pago registrados.
     */
    fun estaVacio(): Boolean {
        return metodosPago.isEmpty()
    }
}
