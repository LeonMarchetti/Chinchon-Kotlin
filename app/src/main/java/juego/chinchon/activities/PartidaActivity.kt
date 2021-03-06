package juego.chinchon.activities

import androidx.fragment.app.FragmentActivity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.util.Log
import android.view.DragEvent
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
class PartidaActivity : FragmentActivity(), IManoFragment {
    companion object {
        const val RC_CORTE = 1
        private const val RC_CAMBIOTURNO = 2
    }

    private val listaManoFragment = ArrayList<ManoFragment>()
    /**
     * Indica si el jugador anterior robo de la pila. Luego, al comienzo del
     * turno muestro el ícono sobre la pila.
     */
    private var roboPila: Boolean = false

    private lateinit var partida: Partida
    private lateinit var rondaActual: Ronda
    private lateinit var turnoActual: Turno

    /**
     * Acción que se realiza al arrastrar una carta sobre otra en el fragmento.
     * Se intercambia de lugar una carta por otra.
     */
    override fun arrastrarCarta(origen: Int, destino: Int) {
        if (origen != destino) {
            turnoActual.intercambiarCartas(origen, destino)
            listaManoFragment[rondaActual.jugadorActual].mostrarMano(turnoActual.jugador.mano)
        }
    }

    /**
     * Inicializa la interfaz de la actividad. Realiza:
     * * Inicia la primera ronda de la partida;
     * * Instancia los fragmentos para las dos manos;
     * * Muestra los datos de los dos jugadores;
     * * Establece los listeners de eventos de los controles;
     * * Establece la imagen del tope de la pila.
     */
    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        setContentView(R.layout.mesajuego)

        partida = intent.getParcelableExtra("PARTIDA") as Partida

        rondaActual = partida.nuevaRonda()
        turnoActual = rondaActual.nuevoTurno()

        //region manoFragment1
        val manoFragment1 = ManoFragment.newInstance()
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.containerMano1, manoFragment1)
                .commit()
        listaManoFragment.add(manoFragment1)
        //endregion

        //region manoFragment2
        val manoFragment2 = ManoFragment.newInstance()
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.containerMano2, manoFragment2)
                .hide(manoFragment2)
                .commit()
        listaManoFragment.add(manoFragment2)
        //endregion

        //region TextViews
        mj_nombrejugador_1.text = partida.jugadores[0].nombre
        mj_puntos_1.text = getString(R.string.mj_puntos, partida.jugadores[0].puntos)

        mj_nombrejugador_2.text = partida.jugadores[1].nombre
        mj_puntos_2.text = getString(R.string.mj_puntos, partida.jugadores[1].puntos)
        //endregion

        //region configurar clickListeners
        mj_mazo.setOnClickListener(mazoClickListener)
        mj_pila.setOnClickListener(pilaClickListener)
        mj_pila.setOnDragListener(pilaDragListener)
        mj_cortar_btn.setOnDragListener(cortarDragListener)
        //endregion

        setTopeMazo(rondaActual.pila, mj_pila, false)
    }

    /**
     * Muestra la mano del primer jugador. En los fragmentos ya está disponible
     * la vista para ser modificada.
     */
    public override fun onStart() {
        super.onStart()
        listaManoFragment[0].mostrarMano(turnoActual.jugador.mano)
    }

    /**
     * Click listener del mazo. Realiza una acción dependiendo de la fase del
     * turno actual en que se encuentra la partida:
     * * **`FaseTurno.ROBAR`**: Roba una carta del mazo.
     * * **`FaseTurno.TIRAR`**: Muestra un diálogo donde ofrece al jugador
     * actual a renunciar al juego, que luego pasa a `GanadorActivity`.
     */
    private val mazoClickListener = View.OnClickListener {
        when (turnoActual.fase) {
            Turno.CREATOR.FaseTurno.ROBAR -> {
                turnoActual.robarCartaMazo()
                listaManoFragment[rondaActual.jugadorActual].mostrarMano(turnoActual.jugador.mano)
                setTopeMazo(rondaActual.mazo, mj_mazo, true)
            }
            Turno.CREATOR.FaseTurno.TIRAR -> {
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

    /**
     * Click listener de la pila. Si la fase del turno actual es
     * `FaseTurno.ROBAR` y la pila no está vacía entonces roba una carta de la
     * pila.
     */
    private val pilaClickListener = View.OnClickListener {
        if (turnoActual.esFaseRobo() && !rondaActual.pila.vacio()) {
            turnoActual.robarCartaPila()
            listaManoFragment[rondaActual.jugadorActual].mostrarMano(turnoActual.jugador.mano)
            setTopeMazo(rondaActual.pila, mj_pila, false)
            roboPila = true
        }
    }

    /**
     * Drag listener de la pila. Cuando el jugador arrastra una carta hacia la
     * pila entonces se tira la carta de la mano a la pila y se sigue con el
     * turno siguiente.
     */
    private val pilaDragListener = View.OnDragListener { view, event ->
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                true
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                (view as ImageView).setColorFilter(ContextCompat.getColor(this, R.color.seleccion))
                true
            }
            DragEvent.ACTION_DRAG_LOCATION -> {
                true
            }
            DragEvent.ACTION_DRAG_EXITED -> {
                (view as ImageView).clearColorFilter()
                view.invalidate()
                true
            }
            DragEvent.ACTION_DROP -> {
                val item = event.clipData.getItemAt(0)
                val dragData = item.text
                (view as ImageView).clearColorFilter()
                view.invalidate()
                val tagOrigen: Int = Integer.parseInt(dragData.toString())

                //region Tirar carta a la pila
                if (turnoActual.esFaseTirar()) {
                    turnoActual.tirarCarta(tagOrigen)
                    listaManoFragment[rondaActual.jugadorActual].mostrarMano(turnoActual.jugador.mano)
                    setTopeMazo(rondaActual.pila, mj_pila, false)
                    turnoActual = rondaActual.nuevoTurno()

                    val intent = Intent(this@PartidaActivity, CambioTurnoActivity::class.java)
                    intent.putExtra("PARTIDA", partida)
                    startActivityForResult(intent, RC_CAMBIOTURNO)
                }
                //endregion
                true
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                (view as ImageView).clearColorFilter()
                view.invalidate()
                true
            }
            else -> {
                Log.d("Chinchon-Kotlin", "Evento desconocido")
                false
            }
        }
    }

    /**
     * Drag listener del botón de corte. Cuando un jugador arrastra una carta
     * hacia este botón entonces se realiza el "corte", pasando a
     * `AcomodarActivity` para acomodar las cartas. Si el jugador hizo chinchón
     * entonces se pasa directamente a `GanadorActivity` para declarar al
     * ganador.
     */
    private val cortarDragListener = View.OnDragListener { view, event ->
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                true
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                view.setBackgroundColor(Color.YELLOW)
                true
            }
            DragEvent.ACTION_DRAG_LOCATION -> {
                true
            }
            DragEvent.ACTION_DRAG_EXITED -> {
                view.background = ContextCompat.getDrawable(this, android.R.drawable.btn_default)
                view.invalidate()
                true
            }
            DragEvent.ACTION_DROP -> {
                val item = event.clipData.getItemAt(0)
                val dragData = item.text
                view.background = ContextCompat.getDrawable(this, android.R.drawable.btn_default)
                view.invalidate()
                val tagOrigen: Int = Integer.parseInt(dragData.toString())

                //region Cortar
                if (turnoActual.esFaseTirar()) {
                    partida.cortar(tagOrigen)
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
                //endregion
                true
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                view.background = ContextCompat.getDrawable(this, android.R.drawable.btn_default)
                view.invalidate()
                true
            }
            else -> {
                Log.d("Chinchon-Kotlin", "Evento desconocido")
                false
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RC_CORTE -> {
                when (resultCode) {
                    //region Ronda nueva
                    1 -> {
                        partida = data!!.getParcelableExtra("PARTIDA") as Partida

                        if (partida.hayGanador) {
                            val intent = Intent(this@PartidaActivity, GanadorActivity::class.java)
                            intent.putExtra("PARTIDA", partida)
                            finish()
                            startActivity(intent)
                        }

                        rondaActual = partida.nuevaRonda()
                        turnoActual = rondaActual.nuevoTurno()

                        //region Actualizar puntaje
                        mj_puntos_1.text = getString(R.string.mj_puntos, partida.jugadores[0].puntos)
                        mj_puntos_2.text = getString(R.string.mj_puntos, partida.jugadores[1].puntos)
                        //endregion

                        //region Actualizar imágenes de mazos
                        setTopeMazo(rondaActual.mazo, mj_mazo, true)
                        setTopeMazo(rondaActual.pila, mj_pila, false)
                        roboPila = false
                        mj_atencion_pila.visibility = View.GONE
                        //endregion

                        val jugadorActual = rondaActual.jugadorActual

                        supportFragmentManager
                                .beginTransaction()
                                .show(listaManoFragment[jugadorActual])
                                .hide(listaManoFragment[1 - jugadorActual])
                                .commit()

                        listaManoFragment[jugadorActual].mostrarMano(partida.jugadores[jugadorActual].mano)

                        mostrarBotonCortar(false)
                    }
                    //endregion
                    //region Resumir turno
                    2 -> {
                        partida.resumir()
                        val jugadorActual = rondaActual.jugadorActual
                        listaManoFragment[jugadorActual].mostrarMano(partida.jugadores[jugadorActual].mano)
                        Toast.makeText(this, getText(R.string.mj_malcorte), Toast.LENGTH_SHORT).show()
                    }
                    //endregion
                }
            }
            RC_CAMBIOTURNO -> {
                //region Turno nuevo
                val jugadorActual = rondaActual.jugadorActual
                supportFragmentManager
                        .beginTransaction()
                        .show(listaManoFragment[jugadorActual])
                        .hide(listaManoFragment[1 - jugadorActual])
                        .commit()
                listaManoFragment[jugadorActual].mostrarMano(partida.jugadores[jugadorActual].mano)

                if (rondaActual.numeroTurno > 2) {
                    mostrarBotonCortar(true)
                }

                if (roboPila) {
                    mj_atencion_pila.visibility = View.VISIBLE
                    roboPila = false
                } else {
                    mj_atencion_pila.visibility = View.GONE
                }
                //endregion
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
