package juego.chinchon.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.Toast
import com.example.leoam.chinchonkotlin.R
import juego.chinchon.Carta
import juego.chinchon.Constantes
import juego.chinchon.Jugador
import juego.chinchon.Mazo
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

        @Suppress("unused")
        private const val TAG = "PartidaActivity"
    }

    private var fase: Fase = Fase.ROBAR_CARTA
    private var carta = CARTA_NOSELECT
    private var numJugador = 0
    private var jugadorInicial = 0
    private var numTurno = TURNO_INICIAL
    private var numRonda = 1
    private var jugadores: ArrayList<Jugador>
    private var mazo: Mazo? = null
    private var pila: Mazo? = null

    private var cartaCorte: Carta? = null

    private val tablas: ArrayList<TableLayout>

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        setContentView(R.layout.mesajuego)

        tablas.add(mj_mano_1)
        tablas.add(mj_mano_2)
        for (t in tablas) {
            setClickListeners(t)
        }
        
        mazo = Mazo(false)
        pila = Mazo(true)

        jugadores = intent.getSerializableExtra(Constantes.INTENT_JUGADORES) as ArrayList<Jugador>
        mazo!!.repartir(jugadores)

        for (i in jugadores.indices) {
            jugadores[i].mano.toTableLayout(tablas[i], false)
        }

        mj_nombrejugador_1.text = this.jugadores[0].nombre
        mj_puntos_1.text = getString(R.string.mj_puntos, this.jugadores[0].puntos)

        mj_nombrejugador_2.text = this.jugadores[1].nombre
        mj_puntos_2.text = getString(R.string.mj_puntos, this.jugadores[1].puntos)

        mj_mazo.setOnClickListener {
            when (fase) {
                Fase.ROBAR_CARTA -> {

                    if (this.mazo!!.cantidad == 0) {
                        this.mazo!!.volcar(pila)
                        this.mazo!!.setImagenTope(mj_mazo, true)
                        this.pila!!.setImagenTope(mj_pila, false)
                    }

                    this.jugadores[numJugador].mano.addCarta(mazo!!.robar())
                    this.fase = Fase.TIRAR_CARTA
                    this.jugadores[numJugador].mano.toTableLayout(tablas[numJugador], true)
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

        mj_pila.setOnClickListener {
            when (fase) {
                Fase.ROBAR_CARTA -> if (!pila!!.vacio()) {
                    this.jugadores[numJugador].mano.addCarta(pila!!.robar())
                    fase = Fase.TIRAR_CARTA
                    this.jugadores[numJugador].mano.toTableLayout(this.tablas[numJugador], true)
                    carta = 0
                    pila!!.setImagenTope(mj_pila, false)
                }
                Fase.TIRAR_CARTA -> if (carta != CARTA_NOSELECT) {
                    pila!!.colocar(this.jugadores[numJugador].mano.tirarCarta(carta)!!)
                    carta = CARTA_NOSELECT

                    mj_nombrecarta.text = ""

                    cambioTurno()
                }
            }
        }

        pila!!.setImagenTope(mj_pila, false)

        mj_cortar_btn.setOnClickListener {
            if (fase == Fase.TIRAR_CARTA) {
                if (carta != CARTA_NOSELECT) {
                    cartaCorte = this.jugadores[numJugador].mano.tirarCarta(carta)

                    val i = Intent(this@PartidaActivity, AcomodarActivity::class.java)
                    i.putExtra(Constantes.INTENT_CORTE, numJugador)
                    i.putExtra(Constantes.INTENT_JUGADORES, this.jugadores)
                    startActivityForResult(i, RC_CORTE)
                }
            }
        }
        numJugador = jugadorInicial
    }

    private fun mostrarBotonCortar() {
        mj_cortar_btn.visibility = View.VISIBLE
    }

    private fun setClickListeners(tabla: TableLayout) {
        for (i in 0..1) {
            val tr = tabla.getChildAt(i) as TableRow
            for (j in 0..3) {
                tr.getChildAt(j).setOnClickListener {
                    val estaCarta: Int = it.tag.toString().toInt()
                    if (!((estaCarta == 8) and (fase == Fase.ROBAR_CARTA))) {
                        if (carta == CARTA_NOSELECT) {
                            carta = estaCarta
                            mj_nombrecarta.text = this.jugadores[numJugador].mano.getCarta(carta).toString()

                        } else { // Intercambio las dos cartas seleccionadas:
                            this.jugadores[numJugador].mano.swapCartas(carta, estaCarta)
                            this.jugadores[numJugador].mano.toTableLayout(this.tablas[numJugador], fase == Fase.TIRAR_CARTA)

                            carta = CARTA_NOSELECT
                            mj_nombrecarta.text = ""
                        }
                    }
                }
            }
        }
    }

    private fun cambioTurno() { // Cambio del jugador actual:
        numJugador = (numJugador + 1) % jugadores.size

        fase = Fase.ROBAR_CARTA

        val intent = Intent(this@PartidaActivity, CambioTurnoActivity::class.java)
        intent.putExtra(Constantes.INTENT_CARTA, pila!!.tope())
        intent.putExtra(Constantes.INTENT_JUGADOR, jugadores[numJugador])
        startActivity(intent)

        if (numJugador == 0) {
            tablas[0].visibility = TableLayout.VISIBLE
            tablas[1].visibility = TableLayout.GONE
        } else {
            tablas[1].visibility = TableLayout.VISIBLE
            tablas[0].visibility = TableLayout.GONE
        }
        jugadores[numJugador].mano.toTableLayout(tablas[numJugador], false)
        if (mazo!!.vacio()) {
            mazo!!.volcar(pila)
        }

        pila!!.setImagenTope(mj_pila, false)

        numTurno++
        if (numTurno > TURNO_INICIAL + 1) {
            mostrarBotonCortar()
        }
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

        val segJugador = 1 - numJugador
        jugadores[numJugador].mano.toTableLayout(tablas[numJugador], false)
        tablas[numJugador].visibility = TableLayout.VISIBLE
        jugadores[segJugador].mano.toTableLayout(tablas[segJugador], false)
        tablas[segJugador].visibility = TableLayout.GONE

        fase = Fase.ROBAR_CARTA
        numTurno = TURNO_INICIAL
        numRonda++

        carta = CARTA_NOSELECT

        mj_nombrecarta.text = ""
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_CORTE) {
            when (resultCode) {
                1 -> {
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
                    jugadores[numJugador].mano.toTableLayout(tablas[numJugador], true)
                    carta = CARTA_NOSELECT

                    mj_nombrecarta.text = ""
                    Toast.makeText(this, getText(R.string.mj_malcorte), Toast.LENGTH_SHORT).show()
                }
                else -> {
                }
            }
        }
    }

    init {
        jugadores = ArrayList()
        tablas = ArrayList()
    }
}