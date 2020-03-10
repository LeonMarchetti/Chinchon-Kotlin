package juego.chinchon.activities

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.example.leoam.chinchonkotlin.R
import juego.chinchon.Constantes
import juego.chinchon.Jugador

import kotlinx.android.synthetic.main.acomodacion.*

/**
 *
 * @author LeoAM
 */
class AcomodarActivity : AppCompatActivity() {

    companion object {
        private enum class Estado(var denominacion: String) {
            DESELECCIONADO("D"),
            EMPAREJADO("E"),
            SELECCIONADO("S");

            override fun toString(): String {
                return denominacion
            }
        }
        private const val CANTIDAD_CARTAS: Int = 7

        @Suppress("unused")
        private const val TAG = "AcomodarActivity"
    }

    private val colorDeseleccionado by lazy { ContextCompat.getColor(this, R.color.ac_deseleccionado) }
    private val colorSeleccionado by lazy { ContextCompat.getColor(this, R.color.ac_seleccionado) }
    private val colorEmparejado by lazy { ContextCompat.getColor(this, R.color.ac_emparejado) }

    private val estados: ArrayList<Estado> = arrayListOf(Estado.DESELECCIONADO, Estado.DESELECCIONADO, Estado.DESELECCIONADO, Estado.DESELECCIONADO, Estado.DESELECCIONADO, Estado.DESELECCIONADO, Estado.DESELECCIONADO)
    private var jugadores: ArrayList<Jugador>? = null
    private var jugadorActual = 0
    private var cortador = 0
    private var cartasSeleccionadas = 0

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        setContentView(R.layout.acomodacion)

        redimensionarCartas()

        @Suppress("UNCHECKED_CAST")
        jugadores = intent.getSerializableExtra(Constantes.INTENT_JUGADORES) as ArrayList<Jugador>?
        cortador = intent.getIntExtra(Constantes.INTENT_CORTE, 0)

        for (index in 0 until ac_mano.childCount) {
            val frameLayout = ac_mano.getChildAt(index) as FrameLayout
            val imageView = frameLayout.getChildAt(0) as ImageView
            imageView.setOnClickListener(cartaClickListener)
        }

        ac_emparejar_btn.setOnClickListener(emparejarClickListener)
        ac_desarmar_btn.setOnClickListener(desarmarClickListener)
        ac_finalizar_btn.setOnClickListener(finalizarClickListener)

        jugadorActual = cortador

        calcularPuntos()

        setJugadorEnPantalla()
    }

    /**
     * Redimensiona las cartas al ancho actual de la pantalla.
     */
    private fun redimensionarCartas() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        var screenWidth = displayMetrics.widthPixels
        for (index in 0..6) {
            val frameLayout = ac_mano.getChildAt(index) as FrameLayout
            val imageView = frameLayout.getChildAt(0) as ImageView
            imageView.getLayoutParams().width = screenWidth / 4
        }
    }

    private val cartaClickListener: View.OnClickListener = View.OnClickListener {
        val estaCarta: Int = it.tag.toString().toInt() - 1
        if (estaCarta != 8) {
            val estadoCarta = ac_buttonrow.getChildAt(estaCarta)
            when (estados[estaCarta]) {
                Estado.DESELECCIONADO -> {
                    estados[estaCarta] = Estado.SELECCIONADO
                    estadoCarta.setBackgroundColor(colorSeleccionado)
                    cartasSeleccionadas++
                }
                Estado.SELECCIONADO -> {
                    estados[estaCarta] = Estado.DESELECCIONADO
                    estadoCarta.setBackgroundColor(colorDeseleccionado)
                    cartasSeleccionadas--
                }
                Estado.EMPAREJADO -> {
                    // Hacer nada
                }
            }
            if (acomodaCortador()) {
                if (cartasSeleccionadas == 0) {
                    ac_finalizar_btn.setText(R.string.ac_Cancelar)
                } else {
                    ac_finalizar_btn.setText(R.string.ac_Finalizar)
                }
            }
        }
    }

    private val emparejarClickListener: View.OnClickListener = View.OnClickListener {
        val tmpLista = ArrayList<Int>()
        for (i in 0 until CANTIDAD_CARTAS) {
            if (estados[i] == Estado.SELECCIONADO) {
                tmpLista.add(i)
            }
        }

        val indices = IntArray(tmpLista.size)
        for (i in indices.indices) {
            indices[i] = tmpLista[i]
        }

        val mano = jugadores!![jugadorActual].mano
        if (mano.mismoPalo(indices) || mano.mismoValor(indices)) {
            for (carta in tmpLista) {
                estados[carta] = Estado.EMPAREJADO
                ac_buttonrow.getChildAt(carta).setBackgroundColor(colorEmparejado)
            }
            ac_errortext.setText(R.string.ac_acomodado)
            calcularPuntos()

        } else {
            ac_errortext.setText(R.string.ac_noacomodado)
        }
    }

    private val desarmarClickListener: View.OnClickListener = View.OnClickListener {
        for (i in 0 until CANTIDAD_CARTAS) {
            estados[i] = Estado.DESELECCIONADO
            ac_buttonrow.getChildAt(i).setBackgroundColor(colorDeseleccionado)
        }
        calcularPuntos()
    }

    /**
     * Click listener del botón "Finalizar".
     */
    private val finalizarClickListener: View.OnClickListener = View.OnClickListener {
        val jugador = jugadores!![jugadorActual]
        val acomodadas = BooleanArray(7)
        for (carta in 0 until 7) {
            acomodadas[carta] = estados[carta] == Estado.EMPAREJADO
        }

        val puntos = jugador.mano.getPuntos(acomodadas)

        if (acomodaCortador()) {
            if (puntos > 5) {
                val intent = Intent()
                setResult(2, intent)
                finish()
            } else {
                if (puntos == 0) {
                    jugador.restar10()
                } else {
                    jugador.addPuntos(puntos)
                }
                jugadorActual = 1 - jugadorActual
                setJugadorEnPantalla()
                calcularPuntos()
            }
        } else {
            jugador.addPuntos(puntos)

            val intent = Intent()
            intent.putExtra(Constantes.INTENT_JUGADORES, jugadores)
            setResult(1, intent)
            finish()
        }
    }

    /**
     * Determina si el jugador que está acomodando las cartas es el que cortó
     * en esta ronda.
     *
     * @return Si el jugador cortó esta ronda.
     */
    private fun acomodaCortador(): Boolean {
        return jugadorActual == cortador
    }

    private fun setJugadorEnPantalla() {
        val jugador = jugadores!![jugadorActual]

        ac_tv_nombre.text = getString(R.string.ac_nombre, jugador.nombre, jugador.puntos)

        if (cortador == jugadorActual) {
            ac_tv_corte.setText(R.string.ac_corte)
            cartasSeleccionadas = 0

        } else {
            ac_tv_corte.text = ""
        }
        if (jugador.mano.esChinchon() && acomodaCortador()) {
            val intent = Intent(this@AcomodarActivity, GanadorActivity::class.java)
            intent.putExtra(Constantes.INTENT_JUGADORES, jugadores)
            intent.putExtra(Constantes.INTENT_GANADOR, jugadorActual + 1)
            intent.putExtra(Constantes.INTENT_CHINCHON, true)
            startActivity(intent)

        } else {
            jugador.mano.toGridLayout(ac_mano, false)

            for (carta in 0..6) {
                estados[carta] = Estado.DESELECCIONADO
                ac_buttonrow.getChildAt(carta).setBackgroundColor(colorDeseleccionado)
            }
        }
        ac_errortext.text = ""
    }

    /**
     * Calcula los puntos que va a sumar el jugador y los puntos que va a tener
     * en total si acomoda las cartas. Lo muestra en un TextView.
     */
    private fun calcularPuntos() {
        val jugador: Jugador? = jugadores?.get(jugadorActual)
        val puntosAhora = jugador!!.puntos
        val acomodaciones = estados.map { it == Estado.EMPAREJADO }.toBooleanArray()
        val puntosTurno = jugador.mano.getPuntos(acomodaciones)

        if (jugadorActual == cortador && puntosTurno == 0) {
            val puntosDespues = puntosAhora - 10
            ac_tv_puntos.text = getString(R.string.ac_restapuntos, puntosDespues)
        } else {
            val puntosDespues = puntosAhora + puntosTurno
            ac_tv_puntos.text = getString(R.string.ac_sumapuntos, puntosTurno, puntosDespues)
        }
    }
}
