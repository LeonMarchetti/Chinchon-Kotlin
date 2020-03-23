package juego.chinchon.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.example.leoam.chinchonkotlin.R
import juego.chinchon.Partida
import kotlinx.android.synthetic.main.registrojugadores.*


class RegistroJugadoresActivity : AppCompatActivity() {

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        setContentView(R.layout.registrojugadores)

        rj_continuar.setOnClickListener(continuarClickListener)
    }

    private val continuarClickListener = View.OnClickListener {
        //region nombres
        val nombreJugador1: String = if (rj_jugador1.text.toString() == "") {
            getString(R.string.nombre_jugador_1)
        } else {
            rj_jugador1.text.toString()
        }

        val nombreJugador2: String = if (rj_jugador2.text.toString() == "") {
            getString(R.string.nombre_jugador_2)
        } else {
            rj_jugador2.text.toString()
        }

        /*if ((nombreJugador1 == "") or (nombreJugador2 == "")) {
            rj_textview_1.visibility = TextView.VISIBLE
        }*/
        //endregion

        //region puntos
        val strPuntos1 = rj_puntos1.text.toString()
        val puntos1 = if (strPuntos1 == "") { 0 } else { strPuntos1.toInt() }

        val strPuntos2 = rj_puntos2.text.toString()
        val puntos2 = if (strPuntos2 == "") { 0 } else { strPuntos2.toInt() }
        //endregion

        val partida = Partida()
        partida.nuevoJugador(nombreJugador1, puntos1)
        partida.nuevoJugador(nombreJugador2, puntos2)

        //region intent
        val intent = Intent(this@RegistroJugadoresActivity, PartidaActivity::class.java)
        intent.putExtra("PARTIDA", partida)
        startActivity(intent)
        //endregion
    }
}