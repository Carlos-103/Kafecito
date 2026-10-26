
/**
 * Representa un método de pago guardado de forma simulada.
 *
 * Por seguridad, únicamente se almacenan la marca
 * de la tarjeta y sus últimos cuatro dígitos.
 *
 * No se debe almacenar el número completo ni el CVV.
 */
data class MetodoPago(
    val id: Int,
    val marca: String,
    val ultimosCuatroDigitos: String
) {

    init {
        require(id > 0) {
            "El identificador debe ser mayor que cero."
        }

        require(marca.isNotBlank()) {
            "La marca de la tarjeta no puede estar vacía."
        }

        require(ultimosCuatroDigitos.matches(Regex("\\d{4}"))) {
            "Se deben proporcionar exactamente cuatro dígitos."
        }
    }

    /**
     * Devuelve una representación segura de la tarjeta.
     */
    fun obtenerDescripcion(): String {
        return "$marca terminada en $ultimosCuatroDigitos"
    }
}
