package juego.chinchon.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.ArrayList;
import android.content.Intent;
import android.content.res.Resources;
import juego.chinchon.Constantes;
import juego.chinchon.Jugador;

import com.example.leoam.chinchonandroid.R;

public class RegistroJugadoresActivity extends AppCompatActivity {

    private static final int CANT_JUGADORES = 2;

    private TextView tv_error;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.registrojugadores);

        final Button bt_continuar = findViewById(R.id.rj_boton_1);

        // Cuadro de texto que indica algún error en el ingreso de datos:
        tv_error = findViewById(R.id.rj_textview_1);

        bt_continuar.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                boolean cancel = false;

                ArrayList<Jugador> jugadores = new ArrayList<>(CANT_JUGADORES);
                Jugador j;

                // Introduzco al Jugador 1:
                j = setJugador( (EditText) findViewById(R.id.rj_jugador1),
                                (EditText) findViewById(R.id.rj_puntos1)
                );

                if (j != null) {
                    jugadores.add(j);
                } else {
                    cancel = true;
                }

                // Introduzco al Jugador 2:
                j = setJugador( (EditText) findViewById(R.id.rj_jugador2),
                                (EditText) findViewById(R.id.rj_puntos2)
                );

                if (j != null) {
                    jugadores.add(j);
                } else {
                    cancel = true;
                }

                if (cancel) {
                    tv_error.setVisibility(TextView.VISIBLE);
                } else {
                    // Cambio a "PartidaActivity":
                    Intent intent = new Intent(RegistroJugadoresActivity.this,
                            PartidaActivity.class);
                    intent.putExtra(Constantes.INTENT_JUGADORES, jugadores);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * Procedimiento para crear un Jugador con los datos ingresados en la
     * actividad.
     * @param et_jugadores Cuadro de texto del nombre del jugador.
     * @param et_puntos Cuadro de texto con los puntos del jugador.
     * @return Objeto jugador, con el nombre y puntos iniciales especificados.
     * Regresa null si algún valor ingresado es inválido.
     */
    private Jugador setJugador(EditText et_jugadores, EditText et_puntos) {

        String str_nombre = et_jugadores.getText().toString();
        String str_puntos = et_puntos.getText().toString();
        int puntos = 0; // Valor por defecto para el puntaje inicial de cada jugador.
        Resources res = getResources();

        // Compruebo que se haya ingresado un nombre no vacío:
        if (str_nombre.length() == 0) {
            // Error por cuadro de nombre vacío:
            tv_error.setText(res.getString(R.string.rj_errornombre));
            return null;
        }

        // Compruebo que la cantidad de puntos ingresada es válida:
        try {
            puntos = Integer.parseInt(str_puntos);
        }
        catch (NumberFormatException e) {

            if (str_puntos.length() != 0) {
                // Error por puntaje inválido.
                // Innecesario, porque el cuadro de texto solo acepta números.
                tv_error.setText(res.getString(R.string.rj_errorpuntos));
                return null;
            }

        }

        // Creo el objeto Jugador a retornar:
        Jugador j = new Jugador(str_nombre);
        j.addPuntos(puntos);
        return j;
    }
}