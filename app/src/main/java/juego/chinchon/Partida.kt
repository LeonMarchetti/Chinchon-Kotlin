package juego.chinchon

import java.io.Serializable

class Partida : Serializable {
    var jugadores: ArrayList<Jugador>
    var rondas: ArrayList<Ronda>
    val rondaActual: Ronda
        get() = rondas.last()
    var resultado: Resultado

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
    }

    fun nuevoJugador(nombre: String, puntos: Int) {
        val jugador = Jugador(nombre, puntos)
        jugadores.add(jugador)
    }

    fun nuevaRonda() {
        rondas.add(Ronda(rondas.size + 1, jugadores))
    }

    fun ganador(): Jugador? {
        if (resultado == Resultado.GANADOR) {
            val enJuego = ArrayList<Jugador>()
            for (jugador in jugadores) {
                val estaVencido = jugador.estaVencido()
                if (!estaVencido) {
                    enJuego.add(jugador)
                }
            }
            return enJuego[0]
        }
        return null
    }
}