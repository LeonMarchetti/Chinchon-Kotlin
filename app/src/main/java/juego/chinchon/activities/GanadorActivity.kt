package juego.chinchon.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.example.leoam.chinchonkotlin.R
//import juego.chinchon.Constantes
//import juego.chinchon.Jugador
import juego.chinchon.Partida
import kotlinx.android.synthetic.main.ganador.*
import java.lang.IllegalStateException

/**
 * Actividad donde se muestra quien ganó la partida.
 *
 * @author LeoAM
 */
class GanadorActivity : AppCompatActivity() {
    /**
     * Configura la pantalla que muestra el resultado de la partida. Muestra
     * los datos del jugador ganador a arriba y al perdedor abajo, con su
     * nombre y cantidad de puntos. Muestra la cantidad de rondas que tuvo la
     * partida.
     */
    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        setContentView(R.layout.ganador)

        /*@Suppress("UNCHECKED_CAST")
        val jugadores    = intent.getSerializableExtra(Constantes.INTENT_JUGADORES) as ArrayList<Jugador>
        val ganador      = intent.getIntExtra(Constantes.INTENT_GANADOR, Constantes.EMPATE)
        val hizoChinchon = intent.getBooleanExtra(Constantes.INTENT_CHINCHON, false)
        val numRonda     = intent.getIntExtra(Constantes.INTENT_NUMERORONDA, 0)

        g_btn.setOnClickListener(finalizarClickListener)

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
        g_tv_6.text = text6*/

        g_btn.setOnClickListener(finalizarClickListener)

        val partida = intent.getSerializableExtra("PARTIDA") as Partida
        when (partida.resultado) {
            Partida.Companion.Resultado.GANADOR -> {
                //region ganador
                g_tv_1.text = getString(R.string.g_felicitaciones)
                g_tv_2.text = getString(R.string.g_ganador, partida.jugadorGanador?.nombre)
                g_tv_3.text = if (partida.chinchon) {
                    getString(R.string.g_chinchon, partida.jugadorGanador?.puntos)
                } else {
                    getString(R.string.g_puntos, partida.jugadorGanador?.puntos)
                }
                //endregion
                //region perdedor
                val perdedor = partida.perdedores[0]
                g_tv_4.text = getString(R.string.g_contra, perdedor.nombre)
                g_tv_5.text = getString(R.string.g_puntos, perdedor.puntos)
                //endregion

                g_tv_2.textSize = resources.getDimension(R.dimen.g_winner_fontsize)
                g_tv_3.textSize = resources.getDimension(R.dimen.g_winner_fontsize)
            }
            Partida.Companion.Resultado.EMPATE -> {
                g_tv_1.text = getString(R.string.g_empate)
                g_tv_2.text = getString(R.string.g_jugador, partida.jugadores[0].nombre)
                g_tv_3.text = getString(R.string.g_puntos, partida.jugadores[0].puntos)
                g_tv_4.text = getString(R.string.g_jugador, partida.jugadores[1].nombre)
                g_tv_5.text = getString(R.string.g_puntos, partida.jugadores[1].puntos)
            }
            Partida.Companion.Resultado.EN_JUEGO -> {
                throw IllegalStateException("No se puede llegar a esta pantalla si se está todavía en juego.")
            }
        }
        g_tv_6.text = getString(R.string.g_rondas, partida.rondas.size)
    }

    private val finalizarClickListener = View.OnClickListener {
        finish()
    }
}
