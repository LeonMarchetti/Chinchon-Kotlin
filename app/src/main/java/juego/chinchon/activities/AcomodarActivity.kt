package juego.chinchon.activities

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.*
import com.example.leoam.chinchonkotlin.R
import juego.chinchon.Constantes
import juego.chinchon.Jugador
import java.util.*

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

    //private val estados: ArrayList<Estado> = ArrayList<Estado>(CANTIDAD_CARTAS)
    private val estados: ArrayList<Estado> = arrayListOf(Estado.DESELECCIONADO, Estado.DESELECCIONADO, Estado.DESELECCIONADO, Estado.DESELECCIONADO, Estado.DESELECCIONADO, Estado.DESELECCIONADO, Estado.DESELECCIONADO)
    private var jugadores: ArrayList<Jugador>? = null
    private var jugadorActual = 0
    private var cortador = 0

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        setContentView(R.layout.acomodacion)

        jugadores = intent.getSerializableExtra(Constantes.INTENT_JUGADORES) as ArrayList<Jugador>?
        cortador = intent.getIntExtra(Constantes.INTENT_CORTE, 0)

        var fila: TableRow
        for (f in 0 until ac_mano.childCount) {
            fila = ac_mano.getChildAt(f) as TableRow
            for (c in 0 until fila.childCount) {
                fila.getChildAt(c).setOnClickListener {
                    val estaCarta: Int = it.tag.toString().toInt() - 1
                    if (estaCarta != 8) {
                        val estadoCarta = ac_buttonrow.getChildAt(estaCarta)
                        when (estados[estaCarta]) {
                            Estado.DESELECCIONADO -> {
                                estados[estaCarta] = Estado.SELECCIONADO
                                estadoCarta.setBackgroundColor(colorSeleccionado)
                            }
                            Estado.SELECCIONADO -> {
                                estados[estaCarta] = Estado.DESELECCIONADO
                                estadoCarta.setBackgroundColor(colorDeseleccionado)
                            }
                            Estado.EMPAREJADO -> {
                                // Hacer nada
                            }
                        }
                    }
                }
            }
        }

        ac_emparejar_btn.setOnClickListener {
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

            } else {
                ac_errortext.setText(R.string.ac_noacomodado)
            }
        }

        ac_desarmar_btn.setOnClickListener {
            for (i in 0 until CANTIDAD_CARTAS) {
                estados[i] = Estado.DESELECCIONADO
                ac_buttonrow.getChildAt(i).setBackgroundColor(colorDeseleccionado)
            }
        }

        ac_finalizar_btn.setOnClickListener {
            val jugador = jugadores!![jugadorActual]
            val acomodadas = BooleanArray(7)
            for (carta in 0 until 7) {
                acomodadas[carta] = estados[carta] == Estado.EMPAREJADO
            }

            val puntos = jugador.mano.getPuntos(acomodadas)

            if (jugadorActual == cortador) {
                when {
                    puntos == 0 -> {
                        jugador.restar10()
                    }
                    puntos > 5 -> {
                        val intent = Intent()
                        setResult(2, intent)
                        finish()
                    }
                    else -> {
                        jugador.addPuntos(puntos)
                    }
                }
            } else {
                jugador.addPuntos(puntos)
            }
            jugadorActual++
            if (jugadorActual < jugadores!!.size) {
                setJugadorEnPantalla()

            } else {
                val intent = Intent()
                intent.putExtra(Constantes.INTENT_JUGADORES, jugadores)
                setResult(1, intent)
                finish()
            }
        }

        jugadorActual = 0

        setJugadorEnPantalla()
    }

    private fun setJugadorEnPantalla() {
        val jugador = jugadores!![jugadorActual]

        ac_tv_nombre.text = getString(R.string.ac_nombre, jugador.nombre, jugador.puntos)

        if (cortador == jugadorActual) {
            ac_tv_corte.setText(R.string.ac_corte)

        } else {
            ac_tv_corte.text = ""
        }
        if (jugador.mano.esChinchon() && jugadorActual == cortador) {
            val intent = Intent(this@AcomodarActivity, GanadorActivity::class.java)
            intent.putExtra(Constantes.INTENT_JUGADORES, jugadores)
            intent.putExtra(Constantes.INTENT_GANADOR, jugadorActual + 1)
            intent.putExtra(Constantes.INTENT_CHINCHON, true)
            startActivity(intent)

        } else {
            jugador.mano.toTableLayout(ac_mano, false)

            for (carta in 0..6) {
                estados[carta] = Estado.DESELECCIONADO
                ac_buttonrow.getChildAt(carta).setBackgroundColor(colorDeseleccionado)
            }
        }
        ac_errortext.text = ""
    }
}