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
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.ganador);

        ArrayList<Jugador> jugadores = (ArrayList<Jugador>) getIntent().getSerializableExtra(Constantes.INTENT_JUGADORES);
        int ganador = getIntent().getIntExtra(Constantes.INTENT_GANADOR, Constantes.EMPATE);
        boolean hizoChinchon = getIntent().getBooleanExtra(Constantes.INTENT_CHINCHON, false);

        final TextView tv1 = (TextView) findViewById(R.id.g_tv_1);
        final TextView tv2 = (TextView) findViewById(R.id.g_tv_2);
        final TextView tv3 = (TextView) findViewById(R.id.g_tv_3);
        final TextView tv4 = (TextView) findViewById(R.id.g_tv_4);
        final TextView tv5 = (TextView) findViewById(R.id.g_tv_5);

        final Button btn = (Button) findViewById(R.id.g_btn);
        btn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                finish();
            }

        });

        if (jugadores == null) {
            //error
            tv1.setText("¡Error!");
            if (jugadores == null) {
                tv2.setText("No se encontró información de los jugadores.");
            }
        } else {
            switch(ganador) {
                case Constantes.EMPATE:
                    tv1.setText("¡Empate! ¡Felicitaciones a ambos!");
                    tv2.setText("Jugador: " + jugadores.get(0).getNombre());
                    tv3.setText("con " + jugadores.get(0).getPuntos() + " puntos.");
                    tv4.setText("Jugador: " + jugadores.get(1).getNombre());
                    tv5.setText("con " + jugadores.get(1).getPuntos() + " puntos.");
                    break;
                case Constantes.GANADOR_1:
                    tv1.setText("¡Felicitaciones!");

                    tv2.setText("Ganó: " + jugadores.get(0).getNombre());
                    tv2.setTextSize(getResources().getDimension(R.dimen.g_winner_fontsize));

                    if (hizoChinchon) {
                        tv3.setText("con " + jugadores.get(0).getPuntos() + " puntos y haciendo CHINCHÓN");
                    } else {
                        tv3.setText("con " + jugadores.get(0).getPuntos() + " puntos.");
                    }
                    tv3.setTextSize(getResources().getDimension(R.dimen.g_winner_fontsize));

                    tv4.setText("Contra: " + jugadores.get(1).getNombre());
                    tv5.setText("con " + jugadores.get(1).getPuntos() + " puntos.");
                    break;
                case Constantes.GANADOR_2:
                    tv1.setText("¡Felicitaciones!");

                    tv2.setText("Ganó: " + jugadores.get(1).getNombre());
                    tv2.setTextSize(getResources().getDimension(R.dimen.g_winner_fontsize));

                    if (hizoChinchon) {
                        tv3.setText("con " + jugadores.get(1).getPuntos() + " puntos y haciendo CHINCHÓN");
                    } else {
                        tv3.setText("con " + jugadores.get(1).getPuntos() + " puntos.");
                    }
                    tv3.setTextSize(getResources().getDimension(R.dimen.g_winner_fontsize));

                    tv4.setText("Contra: " + jugadores.get(0).getNombre());
                    tv5.setText("con " + jugadores.get(0).getPuntos() + " puntos.");
                    break;
                default:
            }
        }
    }
}
