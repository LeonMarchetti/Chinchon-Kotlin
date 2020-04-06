package juego.chinchon

import android.os.Parcel
import android.os.Parcelable
import java.util.*

/**
 * Clase Mano, representa el conjunto de cartas en "la mano" del jugador.
 * Puede contener hasta 8 cartas, en la cual la octava carta solo se tiene
 * durante el propio turno.
 * @author LeoAM
 */
class Mano(private val cartas: ArrayList<Carta> = ArrayList(8)) : Parcelable {

    @Suppress("UNCHECKED_CAST")
    constructor(parcel: Parcel) : this(parcel.readArrayList(Carta::class.java.classLoader) as ArrayList<Carta>)

    /**
     * Flatten this object in to a Parcel.
     *
     * @param parcel The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     * May be 0 or [.PARCELABLE_WRITE_RETURN_VALUE].
     */
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeList(cartas as List<*>?)
    }

    override fun toString(): String {
        val primeraCarta = cartas[0]
        var resultado = "[$primeraCarta"
        var i = 1
        while (i < cartas.size) {
            val carta = cartas[i]
            resultado = "$resultado, $carta"
            i++
        }
        return "$resultado]"
    }

    /**
     * Agrega una carta a la mano.
     *
     * @param c La carta a agregar a la mano.
     */
    fun addCarta(c: Carta) {
        if (cartas.size < 8) {
            cartas.add(c)
        }
    }

    /**
     * Descarta una carta de la mano, dada la posición de la carta en la mano.
     * La carta seleccionada se coloca en la posición de carta extra, y luego
     * se devuelve.
     * @param n La posición en la mano (1-8)
     * @return La carta a descartar
     */
    fun tirarCarta(n: Int): Carta {
        return cartas.removeAt(n)
    }

    /**
     * Devuelve la carta dada su posición en la mano.
     * @param n Índice de la carta.
     * @return La carta seleccionada.
     */
    fun getCarta(n: Int): Carta? {
        return try {
            cartas[n]
        } catch (e: IndexOutOfBoundsException) {
            null
        }
    }

    /**
     * Intercambia de lugar dos cartas, dadas sus posiciones.
     * @param i Posición de la primera carta. (1-8)
     * @param j Posición de la segunda carta. (1-8)
     */
    fun swapCartas(i: Int, j: Int) {
        val cartaI = cartas[i]
        val cartaJ = cartas[j]
        cartas[i] = cartaJ
        cartas[j] = cartaI
    }

    /**
     * Indica si las cartas dadas (sus posiciones en la mano) tienen el mismo
     * valor numérico, con lo cual forman un juego. Son necesarias 3 cartas
     * como mínimo para el juego, y 4 como máximo.
     *
     * @param indices Las posiciones de las cartas en la mano.
     * @return Si forman el juego de cartas con el mismo valor numérico.
     */
    fun mismoValor(indices: IntArray): Boolean {
        if (indices.size in 3..4) {
            var hayComodin = false
            var valor = 0
            var carta: Carta

            for (indice in indices) {
                carta = cartas[indice]
                if (carta.palo == Palo.Comodin) {
                    if (hayComodin) {
                        return false
                    }
                    hayComodin = true
                } else {
                    if (valor == 0) {
                        valor = carta.valor
                    } else {
                        if (carta.valor != valor) {
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
     *
     * @param indices Las posiciones de las cartas en la mano.
     * @return Si forman el juego de cartas con el mismo palo.
     */
    fun mismoPalo(indices: IntArray): Boolean {
        if (indices.size in 3..7) {
            var carta: Carta
            var hayComodin = false
            var valorMin = Int.MAX_VALUE
            var valorMax = Int.MIN_VALUE
            var paloJuego: Palo? = null

            for (indice in indices) {
                carta = cartas[indice]
                if (carta.palo == Palo.Comodin) {
                    if (hayComodin) {
                        return false
                    }
                    hayComodin = true
                } else {
                    if (paloJuego == null) {
                        paloJuego = carta.palo
                    } else {
                        if (carta.palo != paloJuego) {
                            return false
                        }
                    }

                    if (valorMin == Int.MAX_VALUE) {
                        valorMin = carta.valor
                        valorMax = carta.valor
                    } else {
                        if (carta.valor < valorMin) {
                            valorMin = carta.valor
                        }
                        if (carta.valor > valorMax) {
                            valorMax = carta.valor
                        }
                    }
                }
            }
            return ((valorMax - valorMin) < indices.size)
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
        val palo = cartas[0].palo
        var valorMin = cartas[0].valor
        var valorMax = cartas[0].valor

        for (index in 1 until 7) {
            val carta: Carta = cartas[index]
            if (carta.palo != palo) {
                return false
            }
            if (carta.valor > valorMax) {
                valorMax = carta.valor
            }
            if (carta.valor < valorMin) {
                valorMin = carta.valor
            }
        }

        return (valorMax - valorMin) == 6
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
                    puntos += cartas[i].valor
                }
            }
        }
        return puntos
    }

    /**
     * Éste método vacía el contenido de la mano.
     */
    fun vaciar() {
        cartas.clear()
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Mano> {
        override fun createFromParcel(parcel: Parcel): Mano {
            return Mano(parcel)
        }

        override fun newArray(size: Int): Array<Mano?> {
            return Array(size) { Mano() }
        }
    }
}
