package juego.chinchon.fragments

import android.os.Bundle
import android.app.Fragment
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
import com.example.leoam.chinchonkotlin.R
import juego.chinchon.Mano
import kotlinx.android.synthetic.main.fragment_mano.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val PARAM_MANO = "MANO"

/**
 * A simple [Fragment] subclass.
 * Use the [ManoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ManoFragment : Fragment() {
    /** El índice de la carta seleccionada actualmente. */
//    private var cartaSeleccionada: Int = CARTA_NOSELECT

    /** Arreglo con los estados de selección actuales de todas las cartas. */
    private val estadoSeleccion = Array(8) { EstadoSeleccion.DESELECCIONADO }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @return A new instance of fragment ManoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: Mano) =
                ManoFragment().apply {
                    this.arguments = Bundle().apply {
                        putSerializable(PARAM_MANO, param1)
                    }
                }

        /** Número de indice que indica que no hay ninguna carta seleccionada. */
        const val CARTA_NOSELECT = -1

        enum class EstadoSeleccion(var idRes: Int) {
            DESELECCIONADO(0),
            JUEGO_1(R.drawable.flag_blue),
            JUEGO_2(R.drawable.flag_red),
            SELECCIONADO(R.drawable.check)
        }
    }

    /**
     * Crea la instancia de las vistas de la interface y la devuelve. Además:
     * * Obtiene el objeto `Mano` pasado como parámetro y muestra sus cartas en
     * la pantalla.
     * * Redimensiona los `ImageView` de las cartas.
     * * Configura el estado inicial no seleccionado de las cartas.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_mano, container, false)

        val mano = arguments.getSerializable("MANO") as Mano
        val grillaCartas = v.findViewById(R.id.grillaCartas) as GridLayout

        redimensionarCartas(grillaCartas)

        manoToGridLayout(mano, grillaCartas)

        for (index in 0..7) {
            val frameLayout = grillaCartas.getChildAt(index) as FrameLayout
            val imageView = frameLayout.getChildAt(0)
            imageView.setOnClickListener { cartaImageView ->
                val estaCarta = cartaImageView.tag.toString().toInt()
                val iManoFragmentActivity = activity as IManoFragment
                iManoFragmentActivity.seleccionarCarta(estaCarta)
                /*val estaCarta: Int = cartaImageView.tag.toString().toInt()
                if (cartaSeleccionada == CARTA_NOSELECT) {
                    cartaSeleccionada = estaCarta
                    mostrarIconoSeleccion(cartaSeleccionada, true)
                } else {
                    val iManoFragmentActivity = activity as IManoFragment
                    iManoFragmentActivity.intercambiarCartas(cartaSeleccionada, estaCarta)
                    mostrarIconoSeleccion(cartaSeleccionada, false)
                    cartaSeleccionada = CARTA_NOSELECT
                }*/
            }
        }
        return v
    }

    /**
     * Muestra o esconde el "tick" sobre la carta seleccionada actualmente.
     *
     * @param indice Índice de la carta seleccionada.
     * @param seleccionar Si se selecciona o no la carta.
     */
    private fun mostrarIconoSeleccion(indice: Int, seleccionar: Boolean) {
        val gridLayout: GridLayout = grillaCartas
        val frameLayout = gridLayout.getChildAt(indice) as FrameLayout
        val imageView = frameLayout.getChildAt(1) as ImageView

        if (seleccionar) {
            imageView.setImageResource(R.drawable.check)
        } else {
            imageView.setImageDrawable(null)
        }
    }

    /**
     * Muestra las cartas de la mano en este fragmento.
     *
     * @param mano La mano a mostrar.
     */
    fun mostrarMano(mano: Mano) {
        manoToGridLayout(mano, grillaCartas)
    }

    /**
     * Modifica los ImageView de una GridLayout con las cartas de una mano. La
     * grilla tiene lugar para 8 cartas así que cuando la mano tenga 8 cartas
     * se deja un espacio vacío.
     *
     * @param mano La mano con las cartas a mostrar.
     * @param gridLayout Grilla con los ImageView a modificar.
     */
    private fun manoToGridLayout(mano: Mano, gridLayout: GridLayout) {
        for (index in 0 until 8) {
            val carta = mano.getCarta(index)
            val frameLayout = gridLayout.getChildAt(index) as FrameLayout
            val imageView = frameLayout.getChildAt(0) as ImageView
            val imageId = if (carta == null) {
                0
            } else {
                gridLayout.resources.getIdentifier(carta.imagePath, "drawable", gridLayout.context.packageName)
            }
            imageView.setImageResource(imageId)
        }
    }

    /**
     * Redimensiona los `ImageView` de las cartas para que entren todas en la
     * pantalla, en un formato de cuatro (4) columnas.
     *
     * @param gridLayout La grilla con los `ImageView` a redimensionar.
     */
    private fun redimensionarCartas(gridLayout: GridLayout) {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels
        for (index in 0..7) {
            val frameLayout = gridLayout.getChildAt(index) as FrameLayout
            val imageView = frameLayout.getChildAt(0) as ImageView
            imageView.layoutParams.width = screenWidth / 4
        }
    }

    /** Devuelve el índice de la carta seleccionada actualmente. */
    /*fun getSeleccion(): Int {
        return cartaSeleccionada
    }*/

    /**
     * Selecciona una carta en este fragmento, indicando el tipo de selección y
     * su ícono correspondiente.
     *
     * @param i Índice de la carta seleccionada.
     * @param e Tipo de selección.
     */
    fun seleccionarCarta(i: Int, e: EstadoSeleccion) {
        estadoSeleccion[i] = e
        val frameLayout = grillaCartas.getChildAt(i) as FrameLayout
        val imageView = frameLayout.getChildAt(1) as ImageView
        imageView.setImageResource(e.idRes)
    }

    /** Devuelve el estado de selección de una carta. */
    fun getEstadoSeleccion(i: Int): EstadoSeleccion {
        return estadoSeleccion[i]
    }

    /**
     * Cambia el indice de carta seleccionada a "sin selección" y oculta todos
     * los íconos de selección.
     */
    fun limpiarSeleccion() {
//        cartaSeleccionada = CARTA_NOSELECT
        for (indice in 0..7) {
            estadoSeleccion[indice] = EstadoSeleccion.DESELECCIONADO
            val frameLayout = grillaCartas.getChildAt(indice) as FrameLayout
            val imageView = frameLayout.getChildAt(1) as ImageView
            imageView.setImageDrawable(null)
        }
    }
}
