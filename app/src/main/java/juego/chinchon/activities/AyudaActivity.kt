package juego.chinchon.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.leoam.chinchonkotlin.R
import kotlinx.android.synthetic.main.ayuda.*

class AyudaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ayuda)

        ay_volver.setOnClickListener(volverClickListener)
    }

    /** Click listener del botón "Volver". Vuelve a la pantalla inicial. */
    private val volverClickListener = View.OnClickListener {
        finish()
    }
}
