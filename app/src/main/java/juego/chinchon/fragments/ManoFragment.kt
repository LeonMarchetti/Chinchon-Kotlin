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
import juego.chinchon.activities.IManoFragment
import kotlinx.android.synthetic.main.fragment_mano.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ManoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ManoFragment : Fragment() {
    private var cartaSeleccionada: Int = CARTA_NOSELECT

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
                val estaCarta: Int = cartaImageView.tag.toString().toInt()
                if (cartaSeleccionada == CARTA_NOSELECT) {
                    cartaSeleccionada = estaCarta
                    mostrarIconoSeleccion(cartaSeleccionada, true)
                } else {
                    val partidaActivity = activity as IManoFragment
                    partidaActivity.intercambiarCartas(cartaSeleccionada, estaCarta)
                    mostrarIconoSeleccion(cartaSeleccionada, false)
                    cartaSeleccionada = CARTA_NOSELECT
                }
            }
        }
        return v
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ManoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                ManoFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
        private const val CARTA_NOSELECT = -1
    }

    /**
     * Muestra o esconde el "tick" sobre la carta seleccionada actualmente.
     *
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
}
