package juego.chinchon

import java.io.Serializable

/**
 * Clase Jugador, representa a un jugador que participa en el juego.
 * @author LeoAM
 */
class Jugador(val nombre: String) : Serializable {

    /**
     * @param nombre Nombre del jugador
     * @param puntos Puntos iniciales del jugador
     */
    constructor(nombre: String, puntos: Int) : this(nombre) {
        this.puntos = puntos
    }

    companion object {
        /** Máxima cantidad de puntos que un jugador puede tener: */
        private const val MAX_PUNTOS: Int = 100
    }

    /**
     * Devuelve las cartas (la mano) del jugador
     * @return La mano con las cartas del jugador.
     */
    val mano: Mano = Mano()

    /**
     * Devuelve el puntaje del jugador.
     * @return El puntaje del jugador.
     */
    var puntos = 0
        private set

    fun vaciarMano() {
        mano.vaciar()
    }

    /**
     * Suma los puntos dados al puntaje del jugador.
     * @param n Los puntos a adicionar.
     */
    fun addPuntos(n: Int) {
        puntos += n
    }

    /**
     * Resta 10 puntos del jugador. Sucede cuando un jugador corta y se queda
     * sin cartas sin acomodar.
     */
    fun restar10() {
        puntos -= 10
    }

    /**
     * Método que comprueba si un jugador fue vencido, específicamente si el
     * jugador superó los 100 puntos, que se considera fuera del juego.
     * @return True, si el jugador fue vencido.
     */
    fun estaVencido(): Boolean {
        return puntos > MAX_PUNTOS
    }

    override fun toString(): String {
        return "$nombre ($puntos puntos)"
    }
}