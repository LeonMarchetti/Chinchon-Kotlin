package juego.chinchon.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.*
import com.example.leoam.chinchonkotlin.R
import juego.chinchon.Carta
import juego.chinchon.Constantes
import juego.chinchon.Jugador
import juego.chinchon.Mazo
import juego.chinchon.fragments.ManoFragment
import kotlinx.android.synthetic.main.mesajuego.*

/**
 * Actividad donde transcurre la partida.
 *
 * @author LeoAM
 */
class PartidaActivity : AppCompatActivity(), IManoFragment {
    companion object {
        enum class Fase {
            ROBAR_CARTA,
            TIRAR_CARTA
        }
        private const val TURNO_INICIAL = 1
        private const val RC_CORTE = 1
        private const val RC_CAMBIOTURNO = 2
    }

    private var fase: Fase = Fase.ROBAR_CARTA
    private var numJugador = 0
    private var jugadorInicial = 0
    private var numTurno = TURNO_INICIAL
    private var numRonda = 1
    private var jugadores = ArrayList<Jugador>()
    private var mazo: Mazo = Mazo(false)
    private var pila: Mazo = Mazo(true)
    private val manos = ArrayList<ManoFragment>()

    private lateinit var cartaCorte: Carta

    override fun intercambiarCartas(i: Int, j: Int) {
        val mano = jugadores[numJugador].mano
        mano.swapCartas(i, j)
        manos[numJugador].mostrarMano(mano)
    }

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        setContentView(R.layout.mesajuego)

        @Suppress("UNCHECKED_CAST")
        jugadores = intent.getSerializableExtra(Constantes.INTENT_JUGADORES) as ArrayList<Jugador>
        mazo.repartir(jugadores)

        val manoFragment1 = ManoFragment()
        manoFragment1.arguments = Bundle().apply {
            putSerializable("MANO", jugadores[0].mano)
        }
        fragmentManager
                .beginTransaction()
                .replace(R.id.containerMano1, manoFragment1)
                .commit()
        manos.add(manoFragment1)

        val manoFragment2 = ManoFragment()
        manoFragment2.arguments = Bundle().apply {
            putSerializable("MANO", jugadores[1].mano)
        }
        fragmentManager
                .beginTransaction()
                .replace(R.id.containerMano2, manoFragment2)
                .hide(manoFragment2)
                .commit()
        manos.add(manoFragment2)

        mj_nombrejugador_1.text = this.jugadores[0].nombre
        mj_puntos_1.text = getString(R.string.mj_puntos, this.jugadores[0].puntos)

        mj_nombrejugador_2.text = this.jugadores[1].nombre
        mj_puntos_2.text = getString(R.string.mj_puntos, this.jugadores[1].puntos)

        mj_mazo.setOnClickListener(mazoClickListener)
        mj_pila.setOnClickListener(pilaClickListener)

        pila.setImagenTope(mj_pila, false)

        mj_cortar_btn.setOnClickListener(cortarClickListener)
    }

    private val mazoClickListener = View.OnClickListener {
        when (fase) {
            Fase.ROBAR_CARTA -> {
                if (mazo.cantidad == 0) {
                    mazo.volcar(pila)
                    mazo.setImagenTope(mj_mazo, true)
                    pila.setImagenTope(mj_pila, false)
                }

                val mano = jugadores[numJugador].mano
                mano.addCarta(mazo.robar())
                fase = Fase.TIRAR_CARTA

                manos[numJugador].mostrarMano(mano)

                if (mazo.cantidad == 0) {
                    mazo.setImagenTope(mj_mazo, true)
                }
                manos[numJugador].limpiarSeleccion()
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
            Fase.ROBAR_CARTA -> if (!pila.vacio()) {
                val mano = jugadores[numJugador].mano
                mano.addCarta(pila.robar())
                fase = Fase.TIRAR_CARTA

                manos[numJugador].mostrarMano(mano)

                pila.setImagenTope(mj_pila, false)
            }
            Fase.TIRAR_CARTA ->  {
                val cartaSeleccionada = manos[numJugador].getSeleccion()
                if (cartaSeleccionada != ManoFragment.CARTA_NOSELECT) {
                    pila.colocar(jugadores[numJugador].mano.tirarCarta(cartaSeleccionada))
                    cambioTurno()
                }
            }
        }
        manos[numJugador].limpiarSeleccion()
    }

    /**
     * Evento de click del botón "Cortar". Si el jugador que cortó hizo
     * chinchón entonces lo declara el ganador, sino pasa a la fase de
     * acomodación.
     */
    private val cortarClickListener = View.OnClickListener {
        if (fase == Fase.TIRAR_CARTA) {
            val cartaSeleccionada = manos[numJugador].getSeleccion()
            if (cartaSeleccionada != ManoFragment.CARTA_NOSELECT) {
                cartaCorte = jugadores[numJugador].mano.tirarCarta(cartaSeleccionada)
                manos[numJugador].limpiarSeleccion()

                if (jugadores[numJugador].mano.esChinchon()) {
                    val intent = Intent(this@PartidaActivity, GanadorActivity::class.java)
                    intent.putExtra(Constantes.INTENT_JUGADORES, jugadores)
                    intent.putExtra(Constantes.INTENT_GANADOR, numJugador)
                    intent.putExtra(Constantes.INTENT_CHINCHON, true)
                    intent.putExtra(Constantes.INTENT_NUMERORONDA, numRonda)
                    startActivity(intent)
                } else {
                    val intent = Intent(this@PartidaActivity, AcomodarActivity::class.java)
                    intent.putExtra(Constantes.INTENT_CORTE, numJugador)
                    intent.putExtra(Constantes.INTENT_JUGADORES, this.jugadores)
                    startActivityForResult(intent, RC_CORTE)
                }
            }
        }
    }

    private fun mostrarBotonCortar() {
        mj_cortar_btn.visibility = View.VISIBLE
    }

    private fun ocultarBotonCortar() {
        mj_cortar_btn.visibility = View.INVISIBLE
    }

    private fun cambioTurno() {
        numJugador = (numJugador + 1) % jugadores.size

        fase = Fase.ROBAR_CARTA

        val intent = Intent(this@PartidaActivity, CambioTurnoActivity::class.java)
        intent.putExtra(Constantes.INTENT_CARTA, pila.tope())
        intent.putExtra(Constantes.INTENT_JUGADOR, jugadores[numJugador])
        startActivityForResult(intent, RC_CAMBIOTURNO)
    }

    /**
     * Actualiza la mesa de juego para el inicio de una nueva ronda.
     * Realiza:
     * * Actualiza la muestra de puntos.
     * * Coloca las cartas en el mazo y vacía la pila. Actualiza sus imágenes.
     * * Reparte las cartas entre los jugadores.
     * * Muestra la mano del jugador que le toca jugar y oculta la otra.
     * * Reinicia el conteo de turnos y avanza el conteo de rondas.
     * * Oculta el botón "Cortar"
     */
    private fun cambioRonda() {
        jugadorInicial = 1 - jugadorInicial
        numJugador = jugadorInicial

        mj_puntos_1.text = getString(R.string.mj_puntos, jugadores[0].puntos)
        mj_puntos_2.text = getString(R.string.mj_puntos, jugadores[1].puntos)

        mazo = Mazo(false)
        pila = Mazo(true)

        mazo.setImagenTope(mj_mazo, true)
        pila.setImagenTope(mj_pila, false)

        mazo.repartir(jugadores)
        manos[0].mostrarMano(jugadores[0].mano)
        manos[1].mostrarMano(jugadores[1].mano)

        fragmentManager
                .beginTransaction()
                .show(manos[numJugador])
                .hide(manos[1 - numJugador])
                .commit()

        fase = Fase.ROBAR_CARTA
        numTurno = TURNO_INICIAL
        numRonda++

        manos[numJugador].limpiarSeleccion()

        ocultarBotonCortar()
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
                        manos[numJugador].limpiarSeleccion()

                        Toast.makeText(this, getText(R.string.mj_malcorte), Toast.LENGTH_SHORT).show()
                    }
                }
            }
            RC_CAMBIOTURNO -> {
                fragmentManager
                        .beginTransaction()
                        .show(manos[numJugador])
                        .hide(manos[1 - numJugador])
                        .commit()
                manos[numJugador].mostrarMano(jugadores[numJugador].mano)

                if (mazo.vacio()) {
                    mazo.volcar(pila)
                }

                pila.setImagenTope(mj_pila, false)

                numTurno++
                if (numTurno > TURNO_INICIAL + 1) {
                    mostrarBotonCortar()
                }
            }
        }
    }
}
