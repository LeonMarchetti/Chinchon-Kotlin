package juego.chinchon

import java.io.Serializable
import java.lang.IllegalStateException

class Partida : Serializable {
    var jugadores: ArrayList<Jugador>
    var rondas: ArrayList<Ronda>
    val rondaActual: Ronda
        get() = rondas.last()
    var resultado: Resultado
    var jugadorGanador: Jugador?
    var chinchon: Boolean
    val hayGanador: Boolean
        get() = resultado == Resultado.GANADOR

    companion object {
        enum class Resultado {
            GANADOR,
            EMPATE,
            EN_JUEGO
        }
    }

    init {
        jugadores = ArrayList()
        rondas = ArrayList()
        resultado = Resultado.EN_JUEGO
        jugadorGanador = null
        chinchon = false
    }

    fun nuevoJugador(nombre: String, puntos: Int) {
        val jugador = Jugador(nombre, puntos)
        jugadores.add(jugador)
    }

    fun nuevaRonda(): Ronda {
        val ronda = Ronda(rondas.size + 1, jugadores)
        rondas.add(ronda)
        return ronda
    }

    fun renunciar(i: Int) {
        if (resultado != Resultado.EN_JUEGO) {
            throw IllegalStateException("Solo puede renunciar alguien si se está en juego.")
        }
        val enJuego = ArrayList<Jugador>()
        for (j in 0 until jugadores.size) {
            if (i != j) {
                val jugador = jugadores[j]
                val estaVencido = jugador.estaVencido()
                if (!estaVencido) {
                    enJuego.add(jugador)
                }
            }
        }
        when (enJuego.size) {
            0 -> {
                resultado = Resultado.EMPATE
                jugadorGanador = null
            }
            1 -> {
                resultado = Resultado.GANADOR
                jugadorGanador = enJuego[0]
            }
            else -> {
                // No pasa nada
            }
        }
    }

    /**
     * Cortar durante el turno actual
     */
    fun cortar(i: Int) {
        rondaActual.cortar(i)
        val jugadorCortador = jugadores[rondaActual.cortador!!]
        if (jugadorCortador.mano.esChinchon()) {
            chinchon = true
            resultado = Resultado.GANADOR
            jugadorGanador = jugadorCortador
        }
    }

    fun resumir() {
        rondaActual.resumir()
    }

    /**
     * Fase de acomodación de la ronda actual. Verifica si alguien alcanzó los
     * 100 puntos como resultado.
     *
     * @return Si el jugador que acomodó las cartas es el cortador.
     */
    fun acomodar(i: Int, acomodadas: BooleanArray): Boolean {
        val resultadoAcomodar = rondaActual.acomodar(i, acomodadas)

        val enJuego = jugadores.filter { jugador ->
            !jugador.estaVencido()
        }
        when (enJuego.size) {
            0 -> {
                resultado = Resultado.EMPATE
            }
            1 -> {
                resultado = Resultado.GANADOR
                jugadorGanador = enJuego[i]
            }
            else -> {
                // No hacer nada
            }
        }
        return resultadoAcomodar
    }

    fun perdedores(): List<Jugador> {
        return jugadores.filter { jugador -> jugador.estaVencido() }
    }
}
