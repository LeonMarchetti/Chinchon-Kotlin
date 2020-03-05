package juego.chinchon

/**
 * Clase que enumera los palos que puede tener una carta: Espada, Basto, Oro,
 * Copa o Comodín.
 * @author LeoAM
 */
enum class Palo {
    Espada, Basto, Oro, Copa, Comodin;

    override fun toString(): String {
        return when (this) {
            Espada -> "Espada"
            Basto -> "Basto"
            Oro -> "Oro"
            Copa -> "Copa"
            Comodin -> "Comodín"
        }
    }

    /**
     * Método que devuelve la parte del nombre del archivo de la carta que
     * contiene el palo. El nombre del archivo se conforma por:
     * [getPalopath() + "_" + Carta.valor + "s"]
     *
     * Por ejemplo: "espadas_10s"
     * @return Palo de la carta.
     */
    val paloPath: String
        get() {
            return when (this) {
                Espada -> "espadas"
                Basto -> "bastos"
                Oro -> "oros"
                Copa -> "copas"
                Comodin -> "joker"
            }
        }
}