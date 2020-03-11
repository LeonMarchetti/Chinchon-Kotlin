package juego.chinchon.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*
import com.example.leoam.chinchonkotlin.R
import juego.chinchon.Carta
import juego.chinchon.Constantes
import juego.chinchon.Jugador
import juego.chinchon.Mazo
import juego.chinchon.activities.helper.SharedActivityHelper
import kotlinx.android.synthetic.main.mesajuego.*

/**
 * Actividad donde transcurre la partida.
 *
 * @author LeoAM
 */
class PartidaActivity : AppCompatActivity() {
    companion object {
        enum class Fase {
            ROBAR_CARTA,
            TIRAR_CARTA
        }
        private const val TURNO_INICIAL = 1
        private const val CARTA_NOSELECT = 0
        private const val RC_CORTE = 1
        private const val RC_CAMBIOTURNO = 2

        @Suppress("unused")
        private const val TAG = "PartidaActivity"
    }

    private var fase: Fase = Fase.ROBAR_CARTA
    private var carta = CARTA_NOSELECT
    private var numJugador = 0
    private var jugadorInicial = 0
    private var numTurno = TURNO_INICIAL
    private var numRonda = 1
    private var jugadores = ArrayList<Jugador>()
    private var mazo: Mazo? = null
    private var pila: Mazo? = null

    private var cartaCorte: Carta? = null

    private var manos = ArrayList<GridLayout>()

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        setContentView(R.layout.mesajuego)

        manos.add(mj_mano_1)
        manos.add(mj_mano_2)
        for (mano in manos) {
            setClickListeners(mano)
            SharedActivityHelper.redimensionarCartas(this, mano)
        }

        mazo = Mazo(false)
        pila = Mazo(true)

        @Suppress("UNCHECKED_CAST")
        jugadores = intent.getSerializableExtra(Constantes.INTENT_JUGADORES) as ArrayList<Jugador>
        mazo!!.repartir(jugadores)

        for (i in jugadores.indices) {
           SharedActivityHelper.manoToGridLayout(jugadores[i].mano, manos[i], false)
        }

        mj_nombrejugador_1.text = this.jugadores[0].nombre
        mj_puntos_1.text = getString(R.string.mj_puntos, this.jugadores[0].puntos)

        mj_nombrejugador_2.text = this.jugadores[1].nombre
        mj_puntos_2.text = getString(R.string.mj_puntos, this.jugadores[1].puntos)

        mj_mazo.setOnClickListener(mazoClickListener)
        mj_pila.setOnClickListener(pilaClickListener)

        pila!!.setImagenTope(mj_pila, false)

        mj_cortar_btn.setOnClickListener(cortarClickListener)
        numJugador = jugadorInicial
    }

    private val cartaClickListener = View.OnClickListener { imageView ->
        val estaCarta: Int = imageView.tag.toString().toInt()
        if (!((estaCarta == 8) and (fase == Fase.ROBAR_CARTA))) {
            if (carta == CARTA_NOSELECT) {
                carta = estaCarta
                mj_nombrecarta.text = jugadores[numJugador].mano.getCarta(carta).toString()
                seleccionarCarta(true)

            } else {
                jugadores[numJugador].mano.swapCartas(carta, estaCarta)
                SharedActivityHelper.manoToGridLayout(jugadores[numJugador].mano, manos[numJugador], fase == Fase.TIRAR_CARTA)

                seleccionarCarta(false)

                carta = CARTA_NOSELECT
                mj_nombrecarta.text = ""
            }
        }
    }

    private val mazoClickListener = View.OnClickListener {
        when (fase) {
            Fase.ROBAR_CARTA -> {

                if (this.mazo!!.cantidad == 0) {
                    this.mazo!!.volcar(pila)
                    this.mazo!!.setImagenTope(mj_mazo, true)
                    this.pila!!.setImagenTope(mj_pila, false)
                }

                this.jugadores[numJugador].mano.addCarta(mazo!!.robar())
                this.fase = Fase.TIRAR_CARTA
                SharedActivityHelper.manoToGridLayout(jugadores[numJugador].mano, manos[numJugador], true)
                if (mazo!!.cantidad == 0) {
                    mazo!!.setImagenTope(mj_mazo, true)
                }
                carta = CARTA_NOSELECT

                mj_nombrecarta.text = ""
            }
            Fase.TIRAR_CARTA -> {

                val builder = AlertDialog.Builder(this@PartidaActivity)
                builder
                        .setMessage("¿Desea renunciar?")
                        .setPositiveButton("Si") { _, _ ->
                            val ganador: Int = if (numJugador == 0) {
                                Constantes.GANADOR_2
                            } else {
                                Constantes.GANADOR_1
                            }
                            val i = Intent(this@PartidaActivity, GanadorActivity::class.java)
                            i.putExtra(Constantes.INTENT_JUGADORES, jugadores)
                            i.putExtra(Constantes.INTENT_GANADOR, ganador)
                            finish()
                            startActivity(i)
                        }
                        .setNegativeButton("No", null)

                val dialog = builder.create()
                dialog.show()
            }
        }
    }

    private val pilaClickListener = View.OnClickListener {
        when (fase) {
            Fase.ROBAR_CARTA -> if (!pila!!.vacio()) {
                this.jugadores[numJugador].mano.addCarta(pila!!.robar())
                fase = Fase.TIRAR_CARTA
                SharedActivityHelper.manoToGridLayout(jugadores[numJugador].mano, manos[numJugador], true)
                carta = 0
                pila!!.setImagenTope(mj_pila, false)
            }
            Fase.TIRAR_CARTA -> if (carta != CARTA_NOSELECT) {
                pila!!.colocar(this.jugadores[numJugador].mano.tirarCarta(carta)!!)

                seleccionarCarta(false)

                carta = CARTA_NOSELECT

                mj_nombrecarta.text = ""

                cambioTurno()
            }
        }
    }

    private val cortarClickListener = View.OnClickListener {
        if (fase == Fase.TIRAR_CARTA) {
            if (carta != CARTA_NOSELECT) {
                cartaCorte = this.jugadores[numJugador].mano.tirarCarta(carta)

                seleccionarCarta(false)

                val intent = Intent(this@PartidaActivity, AcomodarActivity::class.java)
                intent.putExtra(Constantes.INTENT_CORTE, numJugador)
                intent.putExtra(Constantes.INTENT_JUGADORES, this.jugadores)
                startActivityForResult(intent, RC_CORTE)
            }
        }
    }

    private fun mostrarBotonCortar() {
        mj_cortar_btn.visibility = View.VISIBLE
    }

    private fun ocultarBotonCortar() {
        mj_cortar_btn.visibility = View.INVISIBLE
    }

    private fun setClickListeners(gridLayout: GridLayout) {
        for (index in 0..7) {
            val frameLayout = gridLayout.getChildAt(index) as FrameLayout
            val imageView = frameLayout.getChildAt(0)
            imageView.setOnClickListener(cartaClickListener)
        }
    }

    private fun cambioTurno() { // Cambio del jugador actual:
        numJugador = (numJugador + 1) % jugadores.size

        fase = Fase.ROBAR_CARTA

        val intent = Intent(this@PartidaActivity, CambioTurnoActivity::class.java)
        intent.putExtra(Constantes.INTENT_CARTA, pila!!.tope())
        intent.putExtra(Constantes.INTENT_JUGADOR, jugadores[numJugador])
        startActivityForResult(intent, RC_CAMBIOTURNO)
    }

    private fun cambioRonda() {
        jugadorInicial = 1 - jugadorInicial
        numJugador = jugadorInicial

        mj_puntos_1.text = getString(R.string.mj_puntos, jugadores[0].puntos)
        mj_puntos_2.text = getString(R.string.mj_puntos, jugadores[1].puntos)

        mazo = Mazo(false)
        pila = Mazo(true)

        mazo!!.setImagenTope(mj_mazo, true)
        pila!!.setImagenTope(mj_pila, false)

        mazo!!.repartir(jugadores)

        val segundoJugador = 1 - numJugador
        SharedActivityHelper.manoToGridLayout(jugadores[numJugador].mano, manos[numJugador], false)
        manos[numJugador].visibility = TableLayout.VISIBLE
        SharedActivityHelper.manoToGridLayout(jugadores[segundoJugador].mano, manos[segundoJugador], false)
        manos[segundoJugador].visibility = TableLayout.GONE

        fase = Fase.ROBAR_CARTA
        numTurno = TURNO_INICIAL
        numRonda++

        carta = CARTA_NOSELECT

        ocultarBotonCortar()
        mj_nombrecarta.text = ""
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RC_CORTE -> {
                when (resultCode) {
                    1 -> {
                        @Suppress("UNCHECKED_CAST")
                        jugadores = data.getSerializableExtra(Constantes.INTENT_JUGADORES) as ArrayList<Jugador>

                        val v1 = jugadores[0].estaVencido()
                        val v2 = jugadores[1].estaVencido()
                        if (v1 || v2) {
                            var ganador = Constantes.EMPATE
                            if (v1 xor v2) {
                                ganador = if (v2) Constantes.GANADOR_1 else Constantes.GANADOR_2
                            }

                            val intent = Intent(this@PartidaActivity, GanadorActivity::class.java)
                            intent.putExtra(Constantes.INTENT_JUGADORES, jugadores)
                            intent.putExtra(Constantes.INTENT_GANADOR, ganador)
                            intent.putExtra(Constantes.INTENT_CHINCHON, false)
                            intent.putExtra(Constantes.INTENT_NUMERORONDA, numRonda)
                            finish()
                            startActivity(intent)
                        }
                        cambioRonda()
                    }
                    2 -> {
                        jugadores[numJugador].mano.addCarta(cartaCorte)
                        SharedActivityHelper.manoToGridLayout(jugadores[numJugador].mano, manos[numJugador], true)
                        carta = CARTA_NOSELECT

                        mj_nombrecarta.text = ""
                        Toast.makeText(this, getText(R.string.mj_malcorte), Toast.LENGTH_SHORT).show()
                    }
                }
            }
            RC_CAMBIOTURNO -> {
                manos[numJugador].visibility = TableLayout.VISIBLE
                manos[1 - numJugador].visibility = TableLayout.GONE
                SharedActivityHelper.manoToGridLayout(jugadores[numJugador].mano, manos[numJugador], false)
                if (mazo!!.vacio()) {
                    mazo!!.volcar(pila)
                }

                pila!!.setImagenTope(mj_pila, false)

                numTurno++
                if (numTurno > TURNO_INICIAL + 1) {
                    mostrarBotonCortar()
                }
            }
        }
    }

    /**
     * Muestra o esconde el "tick" sobre la carta seleccionada actualmente.
     *
     * @param seleccionar Si se selecciona o no la carta.
     */
    private fun seleccionarCarta(seleccionar: Boolean) {
        val gridLayout: GridLayout = manos[numJugador]
        val frameLayout = gridLayout.getChildAt(carta - 1) as FrameLayout
        val imageView = frameLayout.getChildAt(1) as ImageView
        val visibility = if (seleccionar) { View.VISIBLE } else { View.INVISIBLE }
        imageView.visibility = visibility
    }

    init {
        jugadores = ArrayList()
        manos = ArrayList()
    }
}
