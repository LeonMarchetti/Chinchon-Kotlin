package juego.chinchon.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.leoam.chinchonkotlin.R
import juego.chinchon.Constantes
import juego.chinchon.Jugador
import kotlinx.android.synthetic.main.ganador.*

/**
 * Actividad donde se muestra quien ganó la partida.
 *
 * @author LeoAM
 */
class GanadorActivity : AppCompatActivity() {

    companion object {
        @Suppress("unused")
        private const val TAG = "GanadorActivity"
    }

    /**
     * Called when the activity is first created.
     */
    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        setContentView(R.layout.ganador)

        val jugadores = intent.getSerializableExtra(Constantes.INTENT_JUGADORES) as ArrayList<Jugador>
        val ganador = intent.getIntExtra(Constantes.INTENT_GANADOR, Constantes.EMPATE)
        val hizoChinchon = intent.getBooleanExtra(Constantes.INTENT_CHINCHON, false)
        val numRonda = intent.getIntExtra(Constantes.INTENT_NUMERORONDA, 0)

        g_btn.setOnClickListener {
            finish()
        }
        val text1: String
        val text2: String
        val text3: String
        val text4: String
        val text5: String
        val text6: String

        if (ganador == Constantes.EMPATE) {
            text1 = getString(R.string.g_empate)
            text2 = getString(R.string.g_jugador, jugadores[0].nombre)
            text3 = getString(R.string.g_puntos, jugadores[0].puntos)
            text4 = getString(R.string.g_jugador, jugadores[1].nombre)
            text5 = getString(R.string.g_puntos, jugadores[1].puntos)

        } else {
            val perdedor = 1 - ganador

            text1 = getString(R.string.g_felicitaciones)
            text2 = getString(R.string.g_ganador, jugadores[ganador].nombre)
            text3 = if (hizoChinchon) {
                getString(R.string.g_chinchon, jugadores[ganador].puntos)
            } else {
                getString(R.string.g_puntos, jugadores[ganador].puntos)
            }
            text4 = getString(R.string.g_contra, jugadores[perdedor].nombre)
            text5 = getString(R.string.g_puntos, jugadores[perdedor].puntos)

            g_tv_2.textSize = resources.getDimension(R.dimen.g_winner_fontsize)
            g_tv_3.textSize = resources.getDimension(R.dimen.g_winner_fontsize)
        }
        text6 = getString(R.string.g_rondas, numRonda)

        g_tv_1.text = text1
        g_tv_2.text = text2
        g_tv_3.text = text3
        g_tv_4.text = text4
        g_tv_5.text = text5
        g_tv_6.text = text6
    }
}