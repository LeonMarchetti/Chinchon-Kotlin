package juego.chinchon

import java.io.Serializable
import java.util.*

/**
 * Clase Mano, representa el conjunto de cartas en "la mano" del jugador.
 * Puede contener hasta 8 cartas, en la cual la octava carta solo se tiene
 * durante el propio turno.
 * @author LeoAM
 */
class Mano internal constructor() : Serializable {

    companion object {
        @Suppress("unused")
        private const val TAG = "Mano"
    }

    private val cartas: ArrayList<Carta?> = ArrayList(7)
    private var cartaExtra: Carta? = null
    /**
     * Agrega una carta a la mano. Si es que la mano tiene menos de 7 cartas
     * (durante el reparto inicial) entonces la agrega directamente. Si tiene ya
     * las 7 cartas, entonces se agrega como una carta "extra".
     * @param c La carta a agregar a la mano.
     */
    fun addCarta(c: Carta?) {
        if (c != null) {
            if (cartas.size >= 7) { //Si la mano está llena (pleno juego) lo asigno a la carta extra.
                cartaExtra = Carta(c)
                return
            } else {
                cartas.add(c)
            }
        }
        cartas.size
    }

    /**
     * Descarta una carta de la mano, dada la posición de la carta en la mano.
     * La carta seleccionada se coloca en la posición de carta extra, y luego
     * se devuelve.
     * @param n La posición en la mano (1-8)
     * @return La carta a descartar
     */
    fun tirarCarta(n: Int): Carta? {
        val n2 = n - 1
        return if (esIndice(n2)) {
            if (n2 != 7) { //Intercambio la carta a tirar con la carta extra
                val tmp = cartas[n2]
                cartas[n2] = cartaExtra
                cartaExtra = tmp
            }
            cartaExtra
        } else {
            null
        }
    }

    /**
     * Devuelve la carta dada su posición en la mano.
     * @param n Índice de la carta.
     * @return La carta seleccionada.
     */
    fun getCarta(n: Int): Carta? {
        val n2 = n - 1
        return if (esIndice(n2)) {
            if (n2 != 7) {
                cartas[n2]
            } else {
                cartaExtra
            }
        } else {
            null
        }
    }

    /**
     * Intercambia de lugar dos cartas, dadas sus posiciones.
     * @param i Posición de la primera carta. (1-8)
     * @param j Posición de la segunda carta. (1-8)
     */
    fun swapCartas(i: Int, j: Int) {
        if (i != j) {
            var max = if (i > j) i else j
            var min = if (max == i) j else i
            max--
            min--
            if (esIndice(max) && esIndice(min)) {
                val tmp = cartas[min]
                if (max == 7) {
                    cartas[min] = cartaExtra
                    cartaExtra = tmp
                } else {
                    cartas[min] = cartas[max]
                    cartas[max] = tmp
                }
            }
        }
    }

    /**
     * Indica si las cartas dadas (sus posiciones en la mano) tienen el mismo
     * valor numérico, con lo cual forman un juego. Son necesarias 3 cartas
     * como mínimo para el juego, y 4 como máximo.
     * @param indices Las posiciones de las cartas en la mano.
     * @return Si forman el juego de cartas con el mismo valor numérico.
     */
    fun mismoValor(indices: IntArray): Boolean {
        if (indices.size == 3 || indices.size == 4) {
            var hayComodin = false
            var valor = 0
            var c: Carta?
            for (indice in indices) {
                c = cartas[indice]
                if (c!!.palo == Palo.Comodin) {
                    if (hayComodin) {
                        return false
                    }
                    hayComodin = true
                } else {
                    if (valor == 0) {
                        valor = c.valor

                    } else {
                        if (c.valor != valor) {
                            return false
                        }
                    }
                }
            }
            return true
        }
        return false
    }

    /**
     * Indica si las cartas dadas (sus posiciones en la mano) tienen el mismo
     * palo, con lo cual forman un juego. Son necesarias 3 cartas como mínimo
     * para el juego.
     * @param indices Las posiciones de las cartas en la mano.
     * @return Si forman el juego de cartas con el mismo palo.
     */
    fun mismoPalo(indices: IntArray): Boolean {
        if (indices.size in 3..7) {
            val tmp = ArrayList<Carta?>(indices.size)
            var c: Carta?
            var hayComodin = false

            for (indice in indices) {
                c = cartas[indice]
                if (c!!.palo == Palo.Comodin) {
                    if (hayComodin) {
                        return false
                    }
                    hayComodin = true
                } else {
                    tmp.add(c)
                }
            }

            tmp.sortBy { it?.valor }
            c = tmp[0]

            val paloJuego = c!!.palo
            var valorAnt = c.valor

            val tmpSize = tmp.size
            var aplicaComodin = hayComodin
            for (i in 1 until tmpSize) {
                c = tmp[i]
                if (c!!.palo != paloJuego) {
                    return false
                }
                if (c.valor != valorAnt + 1) {
                    aplicaComodin = if (aplicaComodin && c.valor == valorAnt + 2) {
                        false
                    } else {
                        return false
                    }
                }
                valorAnt = c.valor
            }
            return true
        }
        return false
    }

    /**
     * Indica si las cartas de la mano formán chinchón. Se forma chinchón cuando
     * las 7 cartas son del mismo palo y además tienen valores numéricos
     * consecutivos. Ejemplo: 1, 2, 3, 4, 5, 6, 7 de Oro.
     *
     * @return Si hay chinchón en la mano.
     */
    fun esChinchon(): Boolean {
        val tmp = ArrayList(cartas)
        tmp.sortBy { it?.valor }
        val p = cartas[0]!!.palo

        for (i in 1 until tmp.size) {
            if (p != tmp[i]!!.palo ||
                    tmp[i]!!.valor != tmp[i - 1]!!.valor + 1) {
                return false
            }
        }
        return true
    }

    /**
     * Calcula la cantidad de puntos acumulados por las cartas que no pudieron
     * ser acomodadas.
     * @param acomodadas arreglo de bits, uno por carta, que indican cuál fue
     * acomodada en un juego y cuál no.
     * @return La cantidad de puntos a sumar al jugador.
     */
    fun getPuntos(acomodadas: BooleanArray): Int {
        var puntos = 0
        if (acomodadas.size >= 7) {
            for (i in 0..6) {
                if (!acomodadas[i]) {
                    puntos += cartas[i]!!.valor
                }
            }
        }
        return puntos
    }

    private fun esIndice(n: Int): Boolean {
        return n in 0..7
    }

    /**
     * Éste método vacía el contenido de la mano.
     */
    fun vaciar() {
        cartas.clear()
        cartaExtra = null
    }
}
