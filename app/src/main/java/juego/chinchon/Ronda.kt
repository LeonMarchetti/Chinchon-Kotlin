package juego.chinchon

class Ronda(val numero: Int, val jugadores: ArrayList<Jugador>) {
    var pila: Mazo
    var mazo: Mazo
    var turnos: ArrayList<Turno>
    var jugadorActual: Int
    val turnoActual: Turno
        get() = turnos.last()
    var cortador: Int? = null

    init {
        mazo = Mazo(false)
        pila = Mazo(true)
        mazo.repartir(jugadores)
        turnos = ArrayList()
        jugadorActual = jugadores.size - 1
    }

    /** Representación textual de la ronda, que consiste en: `"Ronda n°$numero"` */
    override fun toString(): String {
        return "Ronda n°$numero - ${turnos.size} turnos"
    }

    fun nuevoTurno() {
        jugadorActual = (jugadorActual + 1) % jugadores.size
        val turno = Turno(turnos.size + 1, jugadores[jugadorActual], mazo, pila)
        turnos.add(turno)
    }

    fun formanJuego(i: Int, indices: IntArray): Boolean {
        val mano = jugadores[i].mano
        val mismoPalo = mano.mismoPalo(indices)
        val mismoValor = mano.mismoValor(indices)
        return mismoPalo || mismoValor
    }

    fun acomodarCartas(i: Int, acomodadas: BooleanArray) {
        val jugador = jugadores[i]
        val puntos = jugador.mano.getPuntos(acomodadas)

        if (cortador == i && puntos == 0) {
            jugador.restar10()
        } else {
            jugador.addPuntos(puntos)
        }
    }
}