package juego.chinchon.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.leoam.chinchonandroid.R;

import java.util.ArrayList;

import juego.chinchon.Constantes;
import juego.chinchon.Jugador;

/**
 *
 * @author LeoAM
 */
public class GanadorActivity extends AppCompatActivity {

    /**
     * Called when the activity is first created.
     */
    @Override
    @SuppressWarnings("unchecked")
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.ganador);

        ArrayList<Jugador> jugadores = (ArrayList<Jugador>) getIntent().getSerializableExtra(Constantes.INTENT_JUGADORES);
        int ganador = getIntent().getIntExtra(Constantes.INTENT_GANADOR, Constantes.EMPATE);
        boolean hizoChinchon = getIntent().getBooleanExtra(Constantes.INTENT_CHINCHON, false);

        final TextView tv1 = findViewById(R.id.g_tv_1);
        final TextView tv2 = findViewById(R.id.g_tv_2);
        final TextView tv3 = findViewById(R.id.g_tv_3);
        final TextView tv4 = findViewById(R.id.g_tv_4);
        final TextView tv5 = findViewById(R.id.g_tv_5);

        final Button btn = findViewById(R.id.g_btn);
        btn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                finish();
            }

        });

        if (jugadores == null) {
            //error
            tv1.setText(R.string.g_error);
            tv2.setText(R.string.g_errorinfo);
        } else {
            String text1, text2, text3, text4, text5;

            if (ganador == Constantes.EMPATE) {
                text1 = getString(R.string.g_empate);
                text2 = getString(R.string.g_jugador,jugadores.get(0).getNombre());
                text3 = getString(R.string.g_puntos, jugadores.get(0).getPuntos());
                text4 = getString(R.string.g_jugador,jugadores.get(1).getNombre());
                text5 = getString(R.string.g_puntos, jugadores.get(1).getPuntos());
            } else {
                int perdedor = 1 - ganador;
                text1 = getString(R.string.g_felicitaciones);
                text2 = getString(R.string.g_ganador, jugadores.get(ganador).getNombre());
                if (hizoChinchon) {
                    text3 = getString(R.string.g_chinchon, jugadores.get(ganador).getPuntos());
                } else {
                    text3 = getString(R.string.g_puntos, jugadores.get(ganador).getPuntos());
                }
                text4 = getString(R.string.g_contra, jugadores.get(perdedor).getNombre());
                text5 = getString(R.string.g_puntos, jugadores.get(perdedor).getPuntos());

                tv2.setTextSize(getResources().getDimension(R.dimen.g_winner_fontsize));
                tv3.setTextSize(getResources().getDimension(R.dimen.g_winner_fontsize));
            }
            tv1.setText(text1);
            tv2.setText(text2);
            tv3.setText(text3);
            tv4.setText(text4);
            tv5.setText(text5);
        }
    }
}
