package juego.chinchon.activities.helper

import android.app.Activity
import android.util.DisplayMetrics
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
import juego.chinchon.Mano

class SharedActivityHelper {
    companion object {
        /**
         * Modifica los ImageView de una GridLayout con las cartas de una mano.
         * La mano puede llegar a tener
         *
         * @param mano La mano con las cartas a mostrar.
         * @param gridLayout Grilla con los ImageView a modificar.
         * @param hayOctavaCarta Si es que hay que mostrar la octava carta de la mano.
         */
        fun manoToGridLayout(mano: Mano, gridLayout: GridLayout, hayOctavaCarta: Boolean) {
            val limit: Int = if (hayOctavaCarta) { 8 } else { 7 }
            for (index in 0 until limit) {
                val carta = mano.getCarta(index)
                val frameLayout = gridLayout.getChildAt(index) as FrameLayout
                val imageView = frameLayout.getChildAt(0) as ImageView
                val imageId = gridLayout.resources.getIdentifier(carta?.imagePath, "drawable", gridLayout.context.packageName)
                imageView.setImageResource(imageId)
            }

            if (!hayOctavaCarta) {
                val frameLayout = gridLayout.getChildAt(7) as FrameLayout
                val imageView = frameLayout.getChildAt(0) as ImageView
                imageView.setImageResource(0)
            }
        }

        /**
         * Redimensiona las cartas al ancho actual de la pantalla.
         *
         * @oaram activity Actividad que tenga las cartas a redimensionar.
         * @param gridLayout Contenedor con las cartas a redimensionar.
         */
        fun redimensionarCartas(activity: Activity, gridLayout: GridLayout) {
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
}
