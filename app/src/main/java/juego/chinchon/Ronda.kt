package juego.chinchon

import android.os.Parcel
import android.os.Parcelable
import kotlin.IllegalStateException

/**
 * Clase que representa una ronda en una partida. Maneja los turnos que
 * transcurren dentro de ella, el mazo y la pila.
 *
 * @author LeonMarchetti
 */
class Ronda(private val numero: Int, private var jugadorInicial: Int, internal var jugadores: ArrayList<Jugador>): Parcelable {
    lateinit var pila: Mazo
    lateinit var mazo: Mazo
    internal lateinit var turnos: ArrayList<Turno>
    var jugadorActual: Int = 0
    private val turnoActual: Turno
        get() = turnos.last()
    val numeroTurno: Int
        get() = turnos.size
    var cortador: Int? = null
    /**
     * La carta que el cortador tiró al momento del corte. Se mantiene en
     * memoria por si se cancela el corte y hay que volver a agregarla a la
     * mano.
     */
    private var cartaCorte: Carta? = null

    /**
     * Inicializa la ronda, configurando:
     * * El mazo completo, y con las cartas repartidas a los jugadores;
     * * La pila, como un mazo vacío;
     * * La lista de turnos vacía;
     * * El número del primer jugador.
     */
    fun iniciar() {
        mazo = Mazo(false)
        pila = Mazo(true)
        mazo.repartir(jugadores)
        turnos = ArrayList()
        jugadorActual = (jugadorInicial - 1) % jugadores.size
    }

    /** Representación textual de la ronda, que consiste en: `"Ronda n°$numero"` */
    override fun toString(): String {
        return "Ronda n°$numero - ${turnos.size} turnos"
    }

    /**
     * Inicia un nuevo turno. Lo vincula al jugador siguiente, al mazo y a la
     * pila.
     */
    fun nuevoTurno(): Turno {
        jugadorActual = (jugadorActual + 1) % jugadores.size
        val turno = Turno(turnos.size + 1, jugadores[jugadorActual], mazo, pila)
        turnos.add(turno)
        return turno
    }

    /**
     * Determina si se forma un juego con las cartas seleccionadas.
     *
     * @param i Índice del jugador.
     * @param indices Índices de las cartas seleccionadas.
     */
    fun formanJuego(i: Int, indices: IntArray): Boolean {
        val mano = jugadores[i].mano
        val mismoPalo = mano.mismoPalo(indices)
        val mismoValor = mano.mismoValor(indices)
        return mismoPalo || mismoValor
    }

    /**
     * Cortar durante el turno actual. Se guarda la carta con la que se cortó.
     */
    fun cortar(i: Int) {
        cartaCorte = turnoActual.cortar(i)
        cortador = jugadorActual
    }

    /**
     * Acomoda las cartas del jugador número i de acuerdo a un arreglo booleano
     * que representa las cartas que forman parte de un juego.
     *
     * @return Si el jugador que acomodó las cartas es el cortador.
     */
    @Throws(IllegalStateException::class)
    fun acomodar(i: Int, acomodadas: BooleanArray): Boolean {
        if (cortador == null) {
            throw IllegalStateException("No se apueden acomodar cartas si no se cortó")
        }

        val jugador = jugadores[i]
        val puntos = jugador.mano.getPuntos(acomodadas)

        if (cortador == i) {
            when (puntos) {
                0 -> {
                    jugador.restar10()
                }
                in 1..5 -> {
                    jugador.addPuntos(puntos)
                }
                else -> {
                    throw IllegalStateException("No se puede cortar con más de 5 puntos")
                }
            }
            return true
        } else {
            jugador.addPuntos(puntos)
            return false
        }
    }

    /**
     * Resume el juego luego de que se canceló el corte durante el turno
     * actual.
     */
    fun resumir() {
        turnoActual.resumir(cartaCorte!!)
    }
    @Suppress("UNCHECKED_CAST")
    constructor(parcel: Parcel) : this(
            parcel.readInt(), // numero
            parcel.readInt(), // jugadorInicial
            parcel.readArrayList(Jugador::class.java.classLoader) as ArrayList<Jugador>)
    {
        pila = parcel.readParcelable(Mazo::class.java.classLoader)!!
        mazo = parcel.readParcelable(Mazo::class.java.classLoader)!!
        turnos = parcel.readArrayList(Turno::class.java.classLoader) as ArrayList<Turno>
        jugadorActual = parcel.readInt()
        cortador = parcel.readValue(Int::class.java.classLoader) as? Int
        cartaCorte = parcel.readParcelable(Carta::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(numero)
        parcel.writeInt(jugadorActual)
        parcel.writeList(jugadores as List<*>?)

        parcel.writeParcelable(pila, flags)
        parcel.writeParcelable(mazo, flags)
        parcel.writeList(turnos as List<*>)
        parcel.writeInt(jugadorActual)
        parcel.writeValue(cortador)
        parcel.writeParcelable(cartaCorte, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Ronda> {
        override fun createFromParcel(parcel: Parcel): Ronda {
            return Ronda(parcel)
        }

        override fun newArray(size: Int): Array<Ronda?> {
            return arrayOfNulls(size)
        }
    }
}