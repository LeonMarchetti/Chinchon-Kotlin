package juego.chinchon.activities

import android.app.AlertDialog
//import android.app.ProgressDialog.show
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.*
import com.example.leoam.chinchonkotlin.R
import juego.chinchon.*
import juego.chinchon.fragments.IManoFragment
import juego.chinchon.fragments.ManoFragment
import kotlinx.android.synthetic.main.mesajuego.*

/**
 * Actividad donde transcurre la partida.
 *
 * @author LeoAM
 */
class PartidaActivity : AppCompatActivity(), IManoFragment {
    companion object {
        /*enum class Fase {
            ROBAR_CARTA,
            TIRAR_CARTA
        }*/
//        private const val TURNO_INICIAL = 1
        const val RC_CORTE = 1
        private const val RC_CAMBIOTURNO = 2
        private const val CARTA_NOSELECT = -1
    }

//    private var fase: Fase = Fase.ROBAR_CARTA
//    private var numJugador = 0
//    private var jugadorInicial = 0
//    private var numTurno = TURNO_INICIAL
//    private var numRonda = 1
//    private var jugadores = ArrayList<Jugador>()
//    private var mazo: Mazo = Mazo(false)
//    private var pila: Mazo = Mazo(true)
    private val manos = ArrayList<ManoFragment>()
    private var cartaSeleccionada = CARTA_NOSELECT

//    private lateinit var cartaCorte: Carta
    private lateinit var partida: Partida
    private lateinit var rondaActual: Ronda
    private lateinit var turnoActual: Turno

    /**
     * Al seleccionar una carta de la mano, si ya se había seleccionado otra
     * entonces se intercambian de lugar.
     *
     * No responde si se selecciona la octava `ImageView` y se está en la fase
     * de robo, donde el jugador todavía no tiene su octava carta.
     */
    override fun seleccionarCarta(i: Int) {
//        if (!((i == 7) and (fase == Fase.ROBAR_CARTA))) {
        if (!(i == 7 && turnoActual.esFaseRobo())) {
            val numJugador = rondaActual.jugadorActual
            if (cartaSeleccionada == CARTA_NOSELECT) {
                cartaSeleccionada = i
                manos[numJugador].seleccionarCarta(cartaSeleccionada, ManoFragment.Companion.EstadoSeleccion.SELECCIONADO)
            } else {
                /*val mano = jugadores[numJugador].mano
                mano.swapCartas(carta, i)
                manos[numJugador].mostrarMano(mano)*/
                turnoActual.intercambiarCartas(cartaSeleccionada, i)
                manos[numJugador].mostrarMano(turnoActual.jugador.mano)
                manos[numJugador].seleccionarCarta(cartaSeleccionada, ManoFragment.Companion.EstadoSeleccion.DESELECCIONADO)
                cartaSeleccionada = CARTA_NOSELECT
            }
        }
    }

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        setContentView(R.layout.mesajuego)

        //region intent
        /*@Suppress("UNCHECKED_CAST")
        jugadores = intent.getSerializableExtra(Constantes.INTENT_JUGADORES) as ArrayList<Jugador>
        mazo.repartir(jugadores)*/

        partida = intent.getSerializableExtra("PARTIDA") as Partida
        //endregion

        rondaActual = partida.nuevaRonda()
        turnoActual = rondaActual.nuevoTurno()

        //region manoFragment1
        val manoFragment1 = ManoFragment.newInstance()
        fragmentManager
                .beginTransaction()
                .replace(R.id.containerMano1, manoFragment1)
                .commit()
        manos.add(manoFragment1)
        //endregion

        //region manoFragment2
        val manoFragment2 = ManoFragment.newInstance()
        fragmentManager
                .beginTransaction()
                .replace(R.id.containerMano2, manoFragment2)
                .hide(manoFragment2)
                .commit()
        manos.add(manoFragment2)
        //endregion

        //region TextViews
        /*mj_nombrejugador_1.text = this.jugadores[0].nombre
        mj_puntos_1.text = getString(R.string.mj_puntos, this.jugadores[0].puntos)

        mj_nombrejugador_2.text = this.jugadores[1].nombre
        mj_puntos_2.text = getString(R.string.mj_puntos, this.jugadores[1].puntos)*/
        mj_nombrejugador_1.text = partida.jugadores[0].nombre
        mj_puntos_1.text = getString(R.string.mj_puntos, partida.jugadores[0].puntos)

        mj_nombrejugador_2.text = partida.jugadores[1].nombre
        mj_puntos_2.text = getString(R.string.mj_puntos, partida.jugadores[1].puntos)
        //endregion

        //region configurar clickListeners
        mj_mazo.setOnClickListener(mazoClickListener)
        mj_pila.setOnClickListener(pilaClickListener)
        mj_cortar_btn.setOnClickListener(cortarClickListener)
        //endregion

//        setTopeMazo(pila)
        setTopeMazo(rondaActual.pila, mj_pila, false)
    }

    /**
     * Muestra la mano del primer jugador. En los fragmentos ya está disponible
     * la vista para ser modificada.
     */
    public override fun onStart() {
        super.onStart()
//        manos[0].mostrarMano(jugadores[0].mano)
        manos[0].mostrarMano(turnoActual.jugador.mano)
    }

    private val mazoClickListener = View.OnClickListener {
        /*when (fase) {
            Fase.ROBAR_CARTA -> {
                if (mazo.cantidad == 0) {
                    mazo.volcar(pila)
                    setTopeMazo(mazo)
                    setTopeMazo(pila)
                }

                val mano = jugadores[numJugador].mano
                mano.addCarta(mazo.robar())
                fase = Fase.TIRAR_CARTA

                manos[numJugador].mostrarMano(mano)

                if (mazo.cantidad == 0) {
                    setTopeMazo(mazo)
                }
                manos[numJugador].limpiarSeleccion()
                carta = ManoFragment.CARTA_NOSELECT
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
        }*/
        when (turnoActual.fase) {
            Turno.Companion.FaseTurno.ROBAR -> {
                turnoActual.robarCartaMazo()
                manos[0].mostrarMano(turnoActual.jugador.mano)
                setTopeMazo(rondaActual.mazo, mj_mazo, true)
                cartaSeleccionada = ManoFragment.CARTA_NOSELECT
            }
            Turno.Companion.FaseTurno.TIRAR -> {
                val builder = AlertDialog.Builder(this@PartidaActivity)
                builder
                        .setMessage("¿Desea renunciar?")
                        .setPositiveButton("Si") { _, _ ->
                            partida.renunciar(rondaActual.jugadorActual)
                            val intent = Intent(this@PartidaActivity, GanadorActivity::class.java)
                            intent.putExtra("PARTIDA", partida)
                            finish()
                            startActivity(intent)
                        }
                        .setNegativeButton("No", null)
                val dialog = builder.create()
                dialog.show()
            }
        }
    }

    private val pilaClickListener = View.OnClickListener {
        /*when (fase) {
            Fase.ROBAR_CARTA -> if (!pila.vacio()) {
                val mano = jugadores[numJugador].mano
                mano.addCarta(pila.robar())
                fase = Fase.TIRAR_CARTA

                manos[numJugador].mostrarMano(mano)

                setTopeMazo(pila)
            }
            Fase.TIRAR_CARTA -> {
                if (carta != ManoFragment.CARTA_NOSELECT) {
                    pila.colocar(jugadores[numJugador].mano.tirarCarta(carta))
                    cambioTurno()
                }
            }
        }
        manos[numJugador].limpiarSeleccion()
        carta = ManoFragment.CARTA_NOSELECT
        */
        when (turnoActual.fase) {
            Turno.Companion.FaseTurno.ROBAR -> {
                if (!rondaActual.pila.vacio()) {
                    turnoActual.robarCartaPila()
                    manos[rondaActual.jugadorActual].mostrarMano(turnoActual.jugador.mano)
                    setTopeMazo(rondaActual.pila, mj_pila, false)
                }
            }
            Turno.Companion.FaseTurno.TIRAR -> {
                if (cartaSeleccionada != ManoFragment.CARTA_NOSELECT) {
                    turnoActual.tirarCarta(cartaSeleccionada)

                    val intent = Intent(this@PartidaActivity, CambioTurnoActivity::class.java)
                    intent.putExtra("PARTIDA", partida)
                    startActivityForResult(intent, RC_CAMBIOTURNO)
                }
            }
        }
        manos[rondaActual.jugadorActual].limpiarSeleccion()
        cartaSeleccionada = ManoFragment.CARTA_NOSELECT
    }

    /**
     * Evento de click del botón "Cortar". Si el jugador que cortó hizo
     * chinchón entonces lo declara el ganador, sino pasa a la fase de
     * acomodación.
     */
    private val cortarClickListener = View.OnClickListener {
        /*if ((fase == Fase.TIRAR_CARTA) && (carta != ManoFragment.CARTA_NOSELECT)) {
            cartaCorte = jugadores[numJugador].mano.tirarCarta(carta)
            manos[numJugador].limpiarSeleccion()
            if (jugadores[numJugador].mano.esChinchon()) {
                val intent = Intent(this@PartidaActivity, GanadorActivity::class.java)
                intent.putExtra(Constantes.INTENT_JUGADORES, jugadores)
                intent.putExtra(Constantes.INTENT_GANADOR, numJugador)
                intent.putExtra(Constantes.INTENT_CHINCHON, true)
                intent.putExtra(Constantes.INTENT_NUMERORONDA, numRonda)
                finish()
                startActivity(intent)
            } else {
                val intent = Intent(this@PartidaActivity, AcomodarActivity::class.java)
                intent.putExtra(Constantes.INTENT_CORTE, numJugador)
                intent.putExtra(Constantes.INTENT_JUGADORES, this.jugadores)
                startActivityForResult(intent, RC_CORTE)
            }
        }*/
        if (turnoActual.esFaseTirar() && cartaSeleccionada != ManoFragment.CARTA_NOSELECT) {
            partida.cortar(cartaSeleccionada)
            if (partida.hayGanador) {
                val intent = Intent(this@PartidaActivity, GanadorActivity::class.java)
                intent.putExtra("PARTIDA", partida)
                finish()
                startActivity(intent)
            } else {
                val intent = Intent(this@PartidaActivity, AcomodarActivity::class.java)
                intent.putExtra("PARTIDA", partida)
                startActivityForResult(intent, RC_CORTE)
            }
        }
    }

    /**
     * Muestra/oculta el botón "Cortar". Se oculta el botón de cortar durante
     * el primer turno de cada jugando, ya que no está permitido.
     */
    private fun mostrarBotonCortar(visible: Boolean) {
        mj_cortar_btn.visibility = if (visible) View.VISIBLE else View.INVISIBLE
    }

    /**
     * Se inicia el cambio de turno entre los jugadores, actualizando el número
     * del jugador actual, cambiando la fase a la de robar carta e iniciando
     * la actividad de cambio de turno.
     */
    /*private fun cambioTurno() {
        numJugador = (numJugador + 1) % jugadores.size

        fase = Fase.ROBAR_CARTA

        val intent = Intent(this@PartidaActivity, CambioTurnoActivity::class.java)
        intent.putExtra(Constantes.INTENT_CARTA, pila.tope())
        intent.putExtra(Constantes.INTENT_JUGADOR, jugadores[numJugador])
        startActivityForResult(intent, RC_CAMBIOTURNO)
    }*/

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
    /*private fun cambioRonda() {
        jugadorInicial = 1 - jugadorInicial
        numJugador = jugadorInicial

        mj_puntos_1.text = getString(R.string.mj_puntos, jugadores[0].puntos)
        mj_puntos_2.text = getString(R.string.mj_puntos, jugadores[1].puntos)

        mazo = Mazo(false)
        pila = Mazo(true)

        setTopeMazo(mazo)
        setTopeMazo(pila)

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

        carta = ManoFragment.CARTA_NOSELECT

        mostrarBotonCortar(false)
    }*/

    /**
     * Se ejecuta cuando se regresa de una actividad ejecutada desde esta
     * actividad. Las situaciones contempladas son:
     * * Cuando se vuelve de cortar, desde `AcomodarActivity`:
     *      * Si se cortó bien, se comprueba la puntuación de los jugadores y
     *      si alguno o los dos perdieron entonces se va a `GanadorActivity`;
     *      si no, se muestra un mensaje de error.
     * * Cuando se cambia de turno, desde `CambioTurnoActivity`:
     *      * Se muestra la mano del jugador que le toca jugar y oculta la del
     *      anterior.
     *      * Se comprueba si el mazo está vacío y se actualiza la imagen de la
     *      pila.
     *      * Avanza el número de turnos y muestra el botón "Cortar" si es el
     *      segundo turno del jugador.
     */
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RC_CORTE -> {
                when (resultCode) {
                    // Se cortó bien, ronda nueva
                    1 -> {
                        /*@Suppress("UNCHECKED_CAST")
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
                        cambioRonda()*/

                        partida = data.getSerializableExtra("PARTIDA") as Partida
                        rondaActual = partida.nuevaRonda()
                        turnoActual = rondaActual.nuevoTurno()

                        //region Actualizar puntaje
                        mj_puntos_1.text = getString(R.string.mj_puntos, partida.jugadores[0].puntos)
                        mj_puntos_2.text = getString(R.string.mj_puntos, partida.jugadores[1].puntos)
                        //endregion

                        //region Actualizar imágenes de mazos
                        setTopeMazo(rondaActual.mazo, mj_mazo, true)
                        setTopeMazo(rondaActual.pila, mj_pila, false)
                        //endregion

                        fragmentManager
                                .beginTransaction()
                                .show(manos[rondaActual.jugadorActual])
                                .hide(manos[1 - rondaActual.jugadorActual])
                                .commit()

                        mostrarBotonCortar(false)
                    }
                    // Se cortó mal, se sigue el turno.
                    2 -> {
                        /*jugadores[numJugador].mano.addCarta(cartaCorte)
                        manos[numJugador].limpiarSeleccion()*/

                        partida.resumir()

                        Toast.makeText(this, getText(R.string.mj_malcorte), Toast.LENGTH_SHORT).show()
                    }
                }
            }
            RC_CAMBIOTURNO -> {
                /*fragmentManager
                        .beginTransaction()
                        .show(manos[numJugador])
                        .hide(manos[1 - numJugador])
                        .commit()
                manos[numJugador].mostrarMano(jugadores[numJugador].mano)

                if (mazo.vacio()) {
                    mazo.volcar(pila)
                }
                setTopeMazo(pila)

                numTurno++
                if (numTurno > TURNO_INICIAL + 1) {
                    mostrarBotonCortar(true)
                }

                carta = ManoFragment.CARTA_NOSELECT*/

                val jugadorActual = rondaActual.jugadorActual
                fragmentManager
                        .beginTransaction()
                        .show(manos[jugadorActual])
                        .hide(manos[1 - jugadorActual])
                        .commit()
                manos[jugadorActual].mostrarMano(partida.jugadores[jugadorActual].mano)
                setTopeMazo(rondaActual.pila, mj_pila, false)
                cartaSeleccionada = ManoFragment.CARTA_NOSELECT

                turnoActual = rondaActual.nuevoTurno()
                if (rondaActual.numeroTurno > 2) {
                    mostrarBotonCortar(true)
                }
            }
        }
    }

    /**
     * Define la imagen del mazo o la pila de acuerdo a la carta que esté
     * arriba. Si se trata del mazo entonces se muestra el dorso; si es la pila
     * se muestra la carta. En ambos, si está vacío, no se muestra nada.
     */
    private fun setTopeMazo(mazoArg: Mazo, imageView: ImageView, oculto: Boolean) {
        val name: String = when (oculto) {
            true -> {
                if (mazoArg.vacio()) { "vacio" } else { "dorso" }
            }
            false -> {
                if (mazoArg.vacio()) { "vacio" } else { mazoArg.tope().imagePath }
            }
        }
        imageView.setImageResource(resources.getIdentifier(name, "drawable", packageName))
    }
}
