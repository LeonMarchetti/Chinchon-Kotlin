package juego.chinchon

import android.widget.ImageView
import java.util.*

/**
 * Clase para representar un Mazo de cartas españolas, de 50 cartas. 12 cartas
 * para los cuatro palos más dos comodines.
 * También se utiliza para representar al pozo de descarte, al cual se le
 * colocan las cartas que descarta cada jugador durante el transcurso del juego.
 *
 * @param vacio Indica si hay que construir el mazo vacío (para el pozo de descarte) o lleno (para el mazo principal).
 * @author LeoAM
 */
class Mazo(vacio: Boolean) {
    companion object {
        private const val MAXCartas = 50
    }

    private val cartas: ArrayList<Carta> = ArrayList()

    /**
     * Devuelve la cantidad de cartas en el mazo.
     * @return Devuelve la cantidad de cartas en el mazo.
     */
    var cantidad = 0
        private set

    init {
        if (vacio) {
            cantidad = 0
        } else {
            cantidad = MAXCartas
            setCartas()
            mezclar()
        }
    }

    /**
     * Crea la baraja de cartas y las inserta en este mazo. La baraja consiste
     * en las 12 cartas de los 4 palos y los dos comodines.
     */
    private fun setCartas() {
        for (p in Palo.values()) {
            if (p == Palo.Comodin) {
                break
            }
            for (j in 1..12) {
                cartas.add(Carta(j, p))
            }
        }
        cartas.add(Carta(0, Palo.Comodin))
        cartas.add(Carta(0, Palo.Comodin))
    }

    /**
     * Mezcla las cartas del mazo.
     */
    private fun mezclar() {
        if (cantidad > 1) {
            val rdm = Random()
            var tmp: Carta
            var j: Int
            for (i in cantidad - 1 downTo 1) {
                j = rdm.nextInt(i)
                tmp = cartas[i]
                cartas[i] = cartas[j]
                cartas[j] = tmp
            }
        }
    }

    /**
     * Roba una carta del mazo.
     *
     * @return La carta robada.
     */
    fun robar(): Carta {
        cantidad--
        return cartas.removeAt(0)
    }

    /**
     * Muestra cuál es la carta en el tope del mazo. No la quita.
     *
     * @return La carta en el tope.
     */
    fun tope(): Carta {
        return cartas[0]
    }

    /**
     * Coloca una carta en el tope del mazo.
     *
     * @param c La carta a colocar.
     */
    fun colocar(c: Carta) {
        if (cantidad < MAXCartas) {
            cartas.add(0, c)
            cantidad++
        }
    }

    /**
     * Vuelca las cartas de un mazo en éste. Luego las mezcla.
     *
     * @param m El mazo de donde se sacan las cartas.
     */
    fun volcar(m: Mazo) {
        cantidad = m.cantidad
        m.cantidad = 0
        for (i in 0 until cantidad) {
            cartas.add(m.cartas.removeAt(0))
        }
        mezclar()
    }

    /**
     * Reparte las cartas entre los jugadores. Reparte 8 cartas para el primer
     * jugador y 7 para el segundo.
     *
     * @param jugadores Lista de jugadores.
     */
    fun repartir(jugadores: ArrayList<Jugador>) {
        if (cantidad == MAXCartas) {
            for (jugador in jugadores) {
                jugador.vaciarMano()
            }

            for (i in 0 until 7) {
                for (j in 0 until jugadores.size) {
                    jugadores[j].mano.addCarta(robar())
                }
            }

            jugadores[0].mano.addCarta(robar())
        }
    }

    /**
     * Devuelve la representación en String de este mazo como una lista de
     * todas las cartas actualmente en el mazo, cada una en una línea.
     *
     * @return Representación en String de este mazo.
     */
    override fun toString(): String {
        var str = ""
        for (i in 0 until cantidad) {
            str = str + cartas[i].toString() + "\n"
        }
        return str
    }

    /**
     * Dada un componente de imagen, le coloca la imagen del tope del mazo.
     *
     * @param iv Componente de ImageView.
     * @param oculto Si se trata del mazo de robo, entonces muestra el dorso de
     * la carta en lugar de la imagén de la carta en particular.
     */
    fun setImagenTope(iv: ImageView, oculto: Boolean) {
        val res = iv.resources
        val defPackage = iv.context.packageName
        val defType = "drawable"
        when {
            cartas.isEmpty() -> { // Si el mazo o la pila están vacíos, entonces dejó vacío el espacio para la imagen.
                iv.setImageResource(res.getIdentifier("vacio", defType, defPackage))
            }
            oculto -> {
                iv.setImageResource(res.getIdentifier("dorso", defType, defPackage))
            }
            else -> {
                iv.setImageResource(res.getIdentifier(cartas[0].imagePath, defType, defPackage))
            }
        }
    }

    /**
     * Comprueba que el mazó esté vacío.
     *
     * @return Si el mazo está vacío o no.
     */
    fun vacio(): Boolean {
        return cartas.isEmpty()
    }
}
