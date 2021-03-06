package juego.chinchon.activities

import androidx.fragment.app.FragmentActivity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.leoam.chinchonkotlin.R
import juego.chinchon.Jugador
import juego.chinchon.Partida
import juego.chinchon.fragments.IManoFragment
import juego.chinchon.fragments.ManoFragment
import juego.chinchon.fragments.ManoFragment.Companion.EstadoSeleccion.*

import kotlinx.android.synthetic.main.acomodacion.*

/**
 *
 * @author LeoAM
 */
class AcomodarActivity : FragmentActivity(), IManoFragment {

    companion object {
        /** Cantidad de cartas en la mano. */
        private const val CANTIDAD_CARTAS: Int = 7
    }

    /** Índice del jugador actual. */
    private var jugadorActual = 0
    /**
     * Cantidad de cartas seleccionadas. Lo uso para determinar:
     * * El estado de habilitación del botón "Desarmar".
     * * El texto del botón "Cancelar"/"Finalizar", cuando el jugador actual
     * es el que cortó
     */
    private var cartasSeleccionadas = 0
        set(value) {
            field = value
            ac_desarmar_btn.isEnabled = cartasSeleccionadas != 0
            if (acomodaCortador()) {
                val texto = if (field == 0) { R.string.ac_Cancelar } else { R.string.ac_Finalizar }
                ac_finalizar_btn.setText(texto)
            }
        }
    /**
     * Cantidad de juegos formados hasta el momento. Solo puede haber un
     * máximo de dos juegos.
     */
    private var juegosActuales = 0
    private lateinit var partida: Partida
    private lateinit var manoFragment: ManoFragment

    /**
     * Inicializa la fase de acomodación de cartas.
     * * Inicializa los listeners de los botones.
     * * Pone al cortador como el primer jugador que puede acomodar sus cartas.
     * * Inicializa el fragmento de la mano.
     * * Calcula los puntos en total que se lleva el jugador por todas sus
     * cartas.
     */
    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        setContentView(R.layout.acomodacion)

        partida = intent.getParcelableExtra("PARTIDA") as Partida

        ac_emparejar_btn.setOnClickListener(emparejarClickListener)
        ac_desarmar_btn.setOnClickListener(desarmarClickListener)
        ac_finalizar_btn.setOnClickListener(finalizarClickListener)

        jugadorActual = partida.rondaActual.cortador!!

        manoFragment = ManoFragment.newInstance()
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.containerMano, manoFragment)
                .commit()

        calcularPuntos()
        setInformacionJugador()
    }

    /**
     * Muestra la mano del jugador que cortó. En el fragmento ya está
     * disponible la vista para ser modificada.
     */
    override fun onStart() {
        super.onStart()
        manoFragment.mostrarMano(partida.jugadores[jugadorActual].mano)
    }

    /**
     * Acción que se realiza al arrastrar una carta sobre otra en el fragmento.
     * Si se arrastra una carta sobre otra, entonces se cambian de lugar las
     * cartas. Si se arrastra una carta sobre si misma, o si solamente se hace
     * un toque sobre la carta, entonces la selecciona para tratar de formar un
     * juego.
     */
    override fun arrastrarCarta(origen: Int, destino: Int) {
        if (origen == destino) {
            if (origen in 0 until CANTIDAD_CARTAS) {
                when (manoFragment.getEstadoSeleccion(origen)) {
                    DESELECCIONADO -> {
                        manoFragment.seleccionarCarta(origen, SELECCIONADO)
                        cartasSeleccionadas++
                    }
                    SELECCIONADO -> {
                        manoFragment.seleccionarCarta(origen, DESELECCIONADO)
                        cartasSeleccionadas--
                    }
                    else -> {
                        // Hacer nada
                    }
                }
            }
        } else { // origen != destino
            val mano = partida.jugadores[jugadorActual].mano
            mano.swapCartas(origen, destino)
            manoFragment.mostrarMano(mano)

            val estadoSeleccionOrigen = manoFragment.getEstadoSeleccion(origen)
            val estadoSeleccionDestino = manoFragment.getEstadoSeleccion(destino)
            manoFragment.seleccionarCarta(origen, estadoSeleccionDestino)
            manoFragment.seleccionarCarta(destino, estadoSeleccionOrigen)
        }
    }

    /**
     * Click listener del botón "Emparejar".
     * Genera una lista de indices a partir de las cartas con estado
     * "seleccionado" y las pasa al objeto mano del jugador actual para
     * determinar si las cartas seleccionadas forman un juego. Si es asi,
     * cambia el estado de las cartas a "emparejado" y actualiza el cuadro de
     * texto con los puntos del jugador. Si no, muestra un error.
     */
    private val emparejarClickListener: View.OnClickListener = View.OnClickListener {
        val tmpListaIndices = ArrayList<Int>()
        for (i in 0 until CANTIDAD_CARTAS) {
            val estado = manoFragment.getEstadoSeleccion(i)
            if (estado == SELECCIONADO) {
                tmpListaIndices.add(i)
            }
        }

        val indices = tmpListaIndices.toIntArray()
        if (partida.rondaActual.formanJuego(jugadorActual, indices)) {
            val estadoSeleccionJuego = when(juegosActuales) {
                0 -> JUEGO_1
                1 -> JUEGO_2
                else -> {
                    throw IllegalStateException("Hay más de 2 juegos")
                }
            }
            juegosActuales++
            for (i in indices) {
                manoFragment.seleccionarCarta(i, estadoSeleccionJuego)
            }
            Toast.makeText(this, getText(R.string.ac_acomodado), Toast.LENGTH_SHORT).show()
            calcularPuntos()
        } else {
            Toast.makeText(this, getText(R.string.ac_noacomodado), Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Click listener del botón "Desarmar".
     * Cambia el estado de todas las cartas a "deseleccionado", desarmando los
     * juegos que se hallan formado. Actualiza el textview con los puntos del
     * jugador.
     */
    private val desarmarClickListener: View.OnClickListener = View.OnClickListener {
        manoFragment.limpiarSeleccion()
        juegosActuales = 0
        calcularPuntos()
        cartasSeleccionadas = 0
    }

    /**
     * Click listener del botón "Finalizar". Genera una lista de booleanos con
     * las cartas que fueron emparejadas y las pasa al objeto mano para
     * calcular cuantos puntos hizo. Si es el cortador e hizo más de 5 puntos,
     * entonces vuelve a la partida con un mensaje de error.
     */
    private val finalizarClickListener: View.OnClickListener = View.OnClickListener {
        try {
            if (partida.acomodar(jugadorActual, cartasEmparejadas())) {
                // Acomodó el que cortó, sigo con el otro jugador
                jugadorActual = 1 - jugadorActual
                setJugadorEnPantalla()
                calcularPuntos()
            } else {
                val intent = Intent()
                intent.putExtra("PARTIDA", partida)
                setResult(1, intent)
                finish()
            }
        } catch (e: IllegalStateException) {
            val intent = Intent()
            setResult(2, intent)
            finish()
        }
    }

    /**
     * Determina si el jugador que está acomodando las cartas es el que cortó
     * en esta ronda.
     */
    private fun acomodaCortador(): Boolean {
        return jugadorActual == partida.rondaActual.cortador
    }

    /**
     * Muestra la información del jugador y sus cartas en la pantalla.
     * * Indica si el jugador es el que cortó en la última ronda.
     * * Configura el estado de selección de las cartas para que no esté
     * ninguna seleccionada al inicio.
     */
    private fun setJugadorEnPantalla() {
        juegosActuales = 0
        setInformacionJugador()
        manoFragment.mostrarMano(partida.jugadores[jugadorActual].mano)
        manoFragment.limpiarSeleccion()
        cartasSeleccionadas = 0
    }

    /**
     * Calcula los puntos que va a sumar el jugador y los puntos que va a tener
     * en total si acomoda las cartas. Lo muestra en un TextView.
     */
    private fun calcularPuntos() {
        val jugador: Jugador = partida.jugadores[jugadorActual]
        val puntosAhora = jugador.puntos
        val puntosTurno = jugador.mano.getPuntos(cartasEmparejadas())

        if (acomodaCortador() && puntosTurno == 0) {
            ac_tv_puntos.text = getString(R.string.ac_restapuntos, puntosAhora - 10)
        } else {
            ac_tv_puntos.text = getString(R.string.ac_sumapuntos, puntosTurno, puntosAhora + puntosTurno)
        }
    }

    /**
     * Muestra la información del jugador en la pantalla:
     * * Nombre del jugador y los puntos que tuvo hasta la ronda pasada.
     * * Si fue el que cortó en esta ronda.
     */
    private fun setInformacionJugador() {
        val jugador = partida.jugadores[jugadorActual]

        ac_tv_nombre.text = getString(R.string.ac_nombre, jugador.nombre, jugador.puntos)

        if (acomodaCortador()) {
            ac_tv_corte.setText(R.string.ac_corte)
        } else {
            ac_tv_corte.text = ""
        }
    }

    /**
     * Devuelve un array booleano donde cada `true` representa una carta que
     * forma parte de un juego (está acomodada).
     */
    private fun cartasEmparejadas(): BooleanArray {
        return BooleanArray(CANTIDAD_CARTAS) { i ->
            manoFragment.getEstadoSeleccion(i) == JUEGO_1 ||
            manoFragment.getEstadoSeleccion(i) == JUEGO_2
        }
    }
}
