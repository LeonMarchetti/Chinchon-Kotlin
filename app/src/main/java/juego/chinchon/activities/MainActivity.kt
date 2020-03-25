package juego.chinchon.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import com.example.leoam.chinchonkotlin.R
import kotlinx.android.synthetic.main.main.*

class MainActivity : AppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        //region Click listeners
        main_comenzar.setOnClickListener(comenzarClickListener)
        main_salir.setOnClickListener(salirClickListener)
        //endregion
    }

    /**
     * Click listener del botón "Comenzar". Cambia a la actividad
     * "RegistroJugadoresActivity".
     */
    private val comenzarClickListener = View.OnClickListener {
        val intent = Intent(this@MainActivity, RegistroJugadoresActivity::class.java)
        startActivity(intent)
    }

    /**
     * Click listener del botón "Salir". Cambia a la pantalla inicial del
     * dispositivo.
     */
    private val salirClickListener = View.OnClickListener {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        startActivity(intent)
    }
}
