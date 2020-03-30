package juego.chinchon

import android.os.Parcel
import android.os.Parcelable
import java.lang.IllegalStateException

/**
 * Clase que representa la partida de chinchón. Maneja a los jugadores que
 * participan y las rondas que lo conforman.
 *
 * @author LeonMarchetti
 */
class Partida() : Parcelable {
    var jugadores: ArrayList<Jugador>
        private set
    var rondas: ArrayList<Ronda>
        private set
    val rondaActual: Ronda
        get() = rondas.last()
    var resultado: Resultado
        private set
    var jugadorGanador: Jugador?
        private set
    var chinchon: Boolean
        private set
    val hayGanador: Boolean
        get() = resultado == Resultado.GANADOR
    /**
     * Determina el jugador inicial de una ronda. Para la primer ronda se elije
     * al primer jugador ingresado a la partida.
     */
    private var jugadorInicial: Int
    /**
     * Lista de jugadores que perdieron en la partida. Se actualiza con las
     * llamadas a `cortar`, `renunciar` y `acomodar`.
     */
    var perdedores: ArrayList<Jugador>
        private set

    init {
        jugadores = ArrayList()
        rondas = ArrayList()
        resultado = Resultado.EN_JUEGO
        jugadorGanador = null
        chinchon = false
        jugadorInicial = 0
        perdedores = ArrayList()
    }

    /** Añade a un nuevo jugador a la partida */
    fun nuevoJugador(nombre: String, puntos: Int) {
        val jugador = Jugador(nombre, puntos)
        jugadores.add(jugador)
    }

    /**
     * Inicia una nueva ronda en la partida. Hace avanzar el número del jugador
     * inicial para la próxima ronda.
     */
    fun nuevaRonda(): Ronda {
        val ronda = Ronda(rondas.size + 1, jugadorInicial, jugadores)
        ronda.iniciar()
        jugadorInicial = (jugadorInicial + 1) % jugadores.size
        rondas.add(ronda)
        return ronda
    }

    /**
     * Renuncia un jugador a la partida. Comprueba si hay otros jugadores en
     * juego y establece el resultado de la partida.
     */
    fun renunciar(i: Int) {
        if (resultado != Resultado.EN_JUEGO) {
            throw IllegalStateException("Solo puede renunciar alguien si se está en juego.")
        }

        perdedores.add(jugadores[i])

        val enJuego = jugadores.filter { j ->
            !perdedores.contains(j)
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
     * Cortar durante el turno actual. Comprueba si el jugador hizo chinchón y
     * establece el resutlado de la partida.
     */
    fun cortar(i: Int) {
        rondaActual.cortar(i)
        val jugadorCortador = jugadores[rondaActual.cortador!!]
        if (jugadorCortador.mano.esChinchon()) {
            chinchon = true
            resultado = Resultado.GANADOR
            jugadorGanador = jugadorCortador
            perdedores = jugadores.filter { j ->
                j != jugadorGanador
            } as ArrayList<Jugador>
        }
    }

    /**
     * Resume el juego luego de que se canceló el corte durante la ronda
     * actual.
     */
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
        val jugador = jugadores[i]
        if (jugador.estaVencido() && jugador !in perdedores) {
            perdedores.add(jugador)
        }

        val enJuego = jugadores.filter { j ->
            !j.estaVencido()
        }
        when (enJuego.size) {
            0 -> {
                resultado = Resultado.EMPATE
            }
            1 -> {
                resultado = Resultado.GANADOR
                jugadorGanador = enJuego[0]
            }
            else -> {
                // No hacer nada
            }
        }
        return resultadoAcomodar
    }

    constructor(parcel: Parcel) : this() {
        jugadorInicial = parcel.readInt()
        jugadores = parcel.readArrayList(Jugador::class.java.classLoader) as ArrayList<Jugador>
        rondas = parcel.readArrayList(Ronda::class.java.classLoader) as ArrayList<Ronda>
        resultado = parcel.readSerializable() as Resultado
        jugadorGanador = parcel.readParcelable(Jugador::class.java.classLoader)
        chinchon = parcel.readInt() != 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(jugadorInicial)
        parcel.writeList(jugadores as List<*>?)
        parcel.writeList(rondas as List<*>?)
        parcel.writeSerializable(resultado)
        parcel.writeParcelable(jugadorGanador, flags)

        parcel.writeInt(if (chinchon) { 1 } else { 0 })
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Partida> {
        override fun createFromParcel(parcel: Parcel): Partida {
            return Partida(parcel)
        }

        override fun newArray(size: Int): Array<Partida?> {
            return arrayOfNulls(size)
        }

        /** Estado de una partida. */
        enum class Resultado {
            GANADOR,
            EMPATE,
            EN_JUEGO
        }
    }
}
