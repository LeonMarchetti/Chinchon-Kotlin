package juego.chinchon

import java.io.Serializable

/**
 * Clase Carta, representa una carta, de la baraja española.
 * @author LeoAM
 */
class Carta
/**
 * Constructor para la clase Carta, pasando valor y palo.
 * @param v Valor de la carta a crear.
 * @param p Palo de la carta.
 */ internal constructor(v: Int, p: Palo) : Comparable<Carta>, Serializable {
    /**
     * Devuelve el valor numérico de la carta.
     * @return El valor numérico de la carta.
     */
    val valor: Int

    /**
     * Devuelve el "palo" de la carta (Espada, Basto, Oro o Copa)
     * @return El palo de la carta.
     */
    val palo: Palo = p

    /**
     * Método que devuelve el nombre del archivo que almacena la imagen para
     * esta carta. Se genera cuando se crea el objeto.
     * @return El nombre del archivo de la imagen de esta carta.
     */
    val imagePath: String

    init {
        valor = if (v in 1..12) { v } else { VALORCOMODIN }
        imagePath = if (palo == Palo.Comodin) { palo.paloPath } else { palo.paloPath + "_" + valor + "s" }
    }

    override fun toString(): String {
        return "$valor de $palo"
    }

    override fun compareTo(other: Carta): Int {
        return valor - other.valor
    }

    companion object {
        private const val VALORCOMODIN = 25
    }
}