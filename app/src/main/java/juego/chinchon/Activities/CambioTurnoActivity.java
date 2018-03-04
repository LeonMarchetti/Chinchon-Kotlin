package juego.chinchon.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import juego.chinchon.Carta;
import juego.chinchon.Jugador;
import juego.chinchon.Constantes;

import com.example.leoam.chinchonandroid.R;

/**
 * Actividad que muestra una pantalla intermedia entre dos turnos en una
 * partida.
 * @author LeoAM
 */
public class CambioTurnoActivity extends AppCompatActivity {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.cambioturno);

        Carta carta = (Carta) getIntent()
                .getSerializableExtra(Constantes.INTENT_CARTA);
        Jugador jugador = (Jugador) getIntent()
                .getSerializableExtra(Constantes.INTENT_JUGADOR);

        // Inicializo los componentes para la layout de cambio de turno:
        // ImageView iv_cambio_turno = (ImageView) findViewById(R.id.ct_carta);
        // TextView tv_cambio_turno = (TextView) findViewById(R.id.ct_turno_de);
        // Button btn_cambio_turno = (Button) findViewById(R.id.ct_boton);
        ImageView iv_cambio_turno = findViewById(R.id.ct_carta);
        TextView tv_cambio_turno = findViewById(R.id.ct_turno_de);
        Button btn_cambio_turno = findViewById(R.id.ct_boton);

        btn_cambio_turno.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // se vuelve a la partida:
                finish();
            }

        });

        // Muestro la carta en el tope de la pila al finalizar el turno
        // anterior:
        iv_cambio_turno.setImageResource(
                getResources().getIdentifier(
                        carta.getImagePath(),
                        "drawable",
                        "juego.chinchon"));

        // Muestro el nombre del jugador al que le toca el turno:
        // tv_cambio_turno.setText("Turno de: " + jugador.getNombre());
        tv_cambio_turno.setText(getString(R.string.ct_turno_de, jugador.getNombre()));
    }

}
