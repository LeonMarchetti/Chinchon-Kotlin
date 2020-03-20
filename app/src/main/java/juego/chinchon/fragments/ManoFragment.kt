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

/**
 * A simple [Fragment] subclass.
 * Use the [ManoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ManoFragment : Fragment() {
    /** Arreglo con los estados de selección actuales de todas las cartas. */
    private val estadoSeleccion = Array(8) { EstadoSeleccion.DESELECCIONADO }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment ManoFragment.
         */
        @JvmStatic
        fun newInstance() = ManoFragment().apply {}

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

        val grillaCartas = v.findViewById(R.id.grillaCartas) as GridLayout

        redimensionarCartas(grillaCartas)

        for (index in 0..7) {
            val frameLayout = grillaCartas.getChildAt(index) as FrameLayout
            val imageView = frameLayout.getChildAt(0)
            imageView.setOnClickListener { cartaImageView ->
                val estaCarta = cartaImageView.tag.toString().toInt()
                val iManoFragmentActivity = activity as IManoFragment
                iManoFragmentActivity.seleccionarCarta(estaCarta)
            }
        }
        return v
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
        for (indice in 0..7) {
            estadoSeleccion[indice] = EstadoSeleccion.DESELECCIONADO
            val frameLayout = grillaCartas.getChildAt(indice) as FrameLayout
            val imageView = frameLayout.getChildAt(1) as ImageView
            imageView.setImageDrawable(null)
        }
    }
}
