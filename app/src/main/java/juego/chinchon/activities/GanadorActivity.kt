package juego.chinchon.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import com.example.leoam.chinchonkotlin.R
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
