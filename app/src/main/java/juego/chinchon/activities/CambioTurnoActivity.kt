package juego.chinchon.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import com.example.leoam.chinchonkotlin.R
import juego.chinchon.Partida
import kotlinx.android.synthetic.main.cambioturno.*

/**
 * Actividad que muestra una pantalla intermedia entre dos turnos en una
 * partida.
 *
 * @author LeoAM
 */
class CambioTurnoActivity : AppCompatActivity() {

    companion object {
        @Suppress("unused")
        private const val TAG = "CambioTurnoActivity"
    }

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        this.setContentView(R.layout.cambioturno)

        val partida = intent.getSerializableExtra("PARTIDA") as Partida

        ct_boton.setOnClickListener(continuarClickListener)

        //region Tope de la pila
        val cartaTope = partida.rondaActual.pila.tope()
        ct_carta.setImageResource(resources.getIdentifier(cartaTope.imagePath, "drawable", this.packageName))
        //endregion

        //region Nombre del jugador
        val jugadorActual = partida.rondaActual.jugadorActual
        ct_turno_de.text = getString(R.string.ct_turno_de, partida.jugadores[jugadorActual].nombre)
        //endregion
    }

    private val continuarClickListener = View.OnClickListener {
        val intent = Intent()
        setResult(0, intent)
        finish()
    }
}