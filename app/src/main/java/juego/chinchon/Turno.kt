package juego.chinchon

import java.io.Serializable

class Turno (private val numero: Int, val jugador: Jugador, private val mazo: Mazo, private val pila: Mazo): Serializable {
    private lateinit var cartaPila: Carta
    private lateinit var cartaRobo: Carta
    var fase: FaseTurno

    companion object {
        enum class FaseTurno(private var denominacion: String) {
            ROBAR("Robar"),
            TIRAR("Tirar");

            override fun toString(): String {
                return denominacion
            }
        }
    }

    init {
        fase = FaseTurno.ROBAR
    }

    /**
     * Representación textual de este turno, que consiste en
     * `"Turno n°$numero - $nombreJugador"`.
     */
    override fun toString(): String {
        return "Turno n°$numero - ${jugador.nombre}"
    }

    /** Comprueba que el turno está en la fase de robar. */
    fun esFaseRobo(): Boolean {
        return fase == FaseTurno.ROBAR
    }

    /** Comprueba que el turno está en la fase de tirar. */
    fun esFaseTirar(): Boolean {
        return fase == FaseTurno.TIRAR
    }

    /**
     * Roba una carta del mazo. Si el mazo está vacío al iniciar el turno
     * entonces se vuelca el contenido de la pila en el mazo antes de efectuar
     * el robo. Cambia a la fase de "tirar carta".
     */
    @Throws(IllegalStateException::class)
    fun robarCartaMazo() {
        if (fase != FaseTurno.ROBAR) {
            throw IllegalStateException("Solo se puede robar una carta durante la fase de \"robo\"")
        }
        if (mazo.vacio()) {
            mazo.volcar(pila)
        }
        cartaRobo = mazo.robar()
        jugador.mano.addCarta(cartaRobo)
        fase = FaseTurno.TIRAR
    }

    /**
     * Roba una carta de la pila. Cambia a la fase de "tirar carta". No se
     * permite robar una carta de una pila vacía.
     */
    @Throws(IllegalStateException::class)
    fun robarCartaPila() {
        if (fase != FaseTurno.ROBAR) {
            throw IllegalStateException("Solo se puede robar una carta durante la fase de \"robo\"")
        }
        if (pila.vacio()) {
            throw IllegalStateException("No se puede robar de una pila vacía.")
        }
        cartaRobo = pila.robar()
        jugador.mano.addCarta(cartaRobo)
        fase = FaseTurno.TIRAR
    }

    /** Intercambia el lugar de dos cartas en la mano del jugador. */
    fun intercambiarCartas(i: Int, j: Int) {
        jugador.mano.swapCartas(i, j)
    }

    /** Tira una carta de la mano del jugador a la pila. */
    @Throws(IllegalStateException::class)
    fun tirarCarta(i: Int) {
        if (fase != FaseTurno.TIRAR) {
            throw IllegalStateException("Solo se puede tirar una carta durante la fase de \"tirar\"")
        }
        cartaPila = jugador.mano.tirarCarta(i)
        pila.colocar(cartaPila)
    }

    /** Corta, tirando una carta de la mano del jugador. */
    @Throws(IllegalStateException::class)
    fun cortar(i: Int): Carta {
        if (fase != FaseTurno.TIRAR) {
            throw IllegalStateException("Solo se puede cortar durante la fase de \"tirar\"")
        }
        return jugador.mano.tirarCarta(i)
    }

    @Throws(IllegalStateException::class)
    fun resumir(carta: Carta) {
        if (fase != FaseTurno.TIRAR) {
            throw IllegalStateException("Solo se puede cortar durante la fase de \"tirar\"")
        }
        jugador.mano.addCarta(carta)
    }
}