package juego.chinchon

class Turno (val numero: Int, val jugador: Jugador, val mazo: Mazo, val pila: Mazo) {
    lateinit var cartaPila: Carta
    lateinit var cartaRobo: Carta
    var fase: FaseTurno

    companion object {
        enum class FaseTurno(var denominacion: String) {
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

    /**
     * Roba una carta del mazo. Si el mazo está vacío al iniciar el turno
     * entonces se vuelca el contenido de la pila en el mazo antes de efectuar
     * el robo. Cambia a la fase de "tirar carta".
     */
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
    fun tirarCarta(i: Int) {
        if (fase != FaseTurno.TIRAR) {
            throw IllegalStateException("Solo se puede tirar una carta durante la fase de \"tirar\"")
        }
        cartaPila = jugador.mano.tirarCarta(i)
        pila.colocar(cartaPila)
    }

    /** Corta, tirando una carta de la mano del jugador. */
    fun cortar(i: Int) {
        if (fase != FaseTurno.TIRAR) {
            throw IllegalStateException("Solo se puede cortar durante la fase de \"tirar\"")
        }
        cartaPila = jugador.mano.tirarCarta(i)
    }
}