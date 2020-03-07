package juego.chinchon.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.example.leoam.chinchonkotlin.R
import juego.chinchon.Constantes
import juego.chinchon.Jugador
import kotlinx.android.synthetic.main.registrojugadores.*


class RegistroJugadoresActivity : AppCompatActivity() {

    companion object {
        private const val CANT_JUGADORES = 2
        @Suppress("unused")
        private const val TAG = "RegistroJugadoresActivity"
    }

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        setContentView(R.layout.registrojugadores)

        rj_boton_1.setOnClickListener {
            val nombreJugador1: String = if (rj_jugador1.text.toString() == "") "Jugador 1" else rj_jugador1.text.toString()
            val nombreJugador2: String = if (rj_jugador2.text.toString() == "") "Jugador 2" else rj_jugador2.text.toString()

            if ((nombreJugador1 == "") or (nombreJugador2 == "")) {
                rj_textview_1.visibility = TextView.VISIBLE
            }

            val strPuntos1 = rj_puntos1.text.toString()
            val strPuntos2 = rj_puntos2.text.toString()
            val puntos1: Int = if (strPuntos1 == "") 0 else strPuntos1.toInt()
            val puntos2: Int = if (strPuntos2 == "") 0 else strPuntos2.toInt()

            val jugador1 = Jugador(nombreJugador1, puntos1)
            val jugador2 = Jugador(nombreJugador2, puntos2)

            val jugadores = ArrayList<Jugador>(CANT_JUGADORES)
            jugadores.add(jugador1)
            jugadores.add(jugador2)

            val intent = Intent(this@RegistroJugadoresActivity, PartidaActivity::class.java)
            intent.putExtra(Constantes.INTENT_JUGADORES, jugadores)
            startActivity(intent)
        }
    }
}