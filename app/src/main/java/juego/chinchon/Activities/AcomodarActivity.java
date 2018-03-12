package juego.chinchon.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.leoam.chinchonandroid.R;

import java.util.ArrayList;

import juego.chinchon.Constantes;
import juego.chinchon.Jugador;
import juego.chinchon.Mano;

/**
 *
 * @author LeoAM
 */
public class AcomodarActivity extends AppCompatActivity {

    // Colores de los indicadores de las cartas:
    private int clr_des;  // Carta no seleccionada
    private int clr_sel;  // Carta seleccionada
    private int clr_emp;  // Carta Emparejada

    // Estados de selección de las cartas:
    private int[] estados;  // Indica el estado de selección de cada carta.
    private static final int EST_DESELECCIONADO = 0;
    private static final int EST_SELECCIONADO = 1;
    private static final int EST_EMPAREJADO = 2;

    private ArrayList<Jugador> jugadores;
    private int jugadorActual;
    private int cortador;

    // Componentes de la pantalla:
    private LinearLayout filaBotones;
    private TableLayout tablaMano;
    private TextView errorTV, nombreTV, corteTV;

    private final View.OnClickListener cartaClickListener;

    public AcomodarActivity() {

        this.cartaClickListener = new View.OnClickListener() {

            public void onClick(View v) {
                int estaCarta = Integer.parseInt((String) v.getTag()) - 1;
                if (estaCarta != 8) {
                    View estado_carta = filaBotones.getChildAt(estaCarta);
                    switch (estados[estaCarta]) {
                        case EST_DESELECCIONADO:
                            estados[estaCarta] = EST_SELECCIONADO;
                            estado_carta.setBackgroundColor(clr_sel);
                            break;
                        case EST_SELECCIONADO:
                            estados[estaCarta] = EST_DESELECCIONADO;
                            estado_carta.setBackgroundColor(clr_des);
                            break;
                        default:
                    }
                }
            }
        };
        estados = new int[7];
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.acomodacion);

        jugadores = (ArrayList<Jugador>) getIntent().getSerializableExtra(Constantes.INTENT_JUGADORES);
        cortador = getIntent().getIntExtra(Constantes.INTENT_CORTE, 0);

        tablaMano = findViewById(R.id.ac_mano);
        TableRow fila;
        for (int f = 0; f < tablaMano.getChildCount(); f++) {
            fila = (TableRow) tablaMano.getChildAt(f);
            for (int c = 0; c < fila.getChildCount(); c++) {
                fila.getChildAt(c).setOnClickListener(cartaClickListener);
            }
        }

        filaBotones = findViewById(R.id.ac_buttonrow);

        final Button emparejar = findViewById(R.id.ac_emparejar_btn);
        final Button desarmar = findViewById(R.id.ac_desarmar_btn);
        final Button finalizar = findViewById(R.id.ac_finalizar_btn);

        nombreTV = findViewById(R.id.ac_tv_nombre);
        errorTV = findViewById(R.id.ac_errortext);
        corteTV = findViewById(R.id.ac_tv_corte);

        // Obtengo de R los colores para los indicadores:

        clr_des = ContextCompat.getColor(this, R.color.ac_deseleccionado);
        clr_sel = ContextCompat.getColor(this, R.color.ac_seleccionado);
        clr_emp = ContextCompat.getColor(this, R.color.ac_emparejado);

        emparejar.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                ArrayList<Integer> tmpLista = new ArrayList<>();
                for (int carta = 0; carta < estados.length; carta++) {
                    if (estados[carta] == EST_SELECCIONADO) {
                        tmpLista.add(carta);
                    }
                }

                int[] indices = new int[tmpLista.size()];
                for (int carta = 0; carta < indices.length; carta++) {
                    indices[carta] = tmpLista.get(carta);
                }

                // Paso a comprobar si forman un juego:
                Mano mano = jugadores.get(jugadorActual).getMano();
                if (mano.mismoPalo(indices) || mano.mismoValor(indices)) {
                    for (int carta : tmpLista) {
                        estados[carta] = EST_EMPAREJADO;
                        filaBotones.getChildAt(carta).setBackgroundColor(clr_emp);
                    }
                    errorTV.setText(R.string.ac_acomodado);
                } else {
                    errorTV.setText(R.string.ac_noacomodado);
                }
            }

        });

        desarmar.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                /* Se deseleccionan las cartas, colocando el estado en
                   "deseleccionado" y también se cambian el color de los
                   indicadores. */
                for (int carta = 0; carta < estados.length; carta++) {
                    estados[carta] = EST_DESELECCIONADO;
                    filaBotones.getChildAt(carta).setBackgroundColor(clr_des);
                }
            }

        });

        finalizar.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Jugador jugador = jugadores.get(jugadorActual);
                boolean[] acomodadas = new boolean[7];
                for (int carta = 0; carta < 7; carta++) {
                    acomodadas[carta] = (estados[carta] == EST_EMPAREJADO);
                }

                int puntos = jugador.getMano().getPuntos(acomodadas);

                if (jugadorActual == cortador) {
                    if (puntos == 0) {
                        /* Si el jugador corta y se queda sin cartas sin
                           acomodar entonces puede restar 10. */
                        jugador.restar10();
                    } else if (puntos > 5) {
                        /* Si el jugador corta y se quedó con más de 5 puntos en
                           la mano entonces se cancela el corte y se regresa al
                           mismo juego. */
                        Intent intent = new Intent();
                        setResult(2, intent);
                        finish();
                    } else {
                        jugador.addPuntos(puntos);
                    }
                } else {
                    jugador.addPuntos(puntos);
                }

                jugadorActual++;
                if (jugadorActual < jugadores.size()) {
                    // Cambio de jugador:
                    setJugadorEnPantalla();
                } else {
                    Intent intent = new Intent();
                    intent.putExtra(Constantes.INTENT_JUGADORES, jugadores);
                    setResult(1, intent);
                    finish();
                }
            }
        });

        // Empiezo la acomodación desde el primer jugador:
        jugadorActual = 0;

        /* Empiezo a configurar los views en pantalla, de acuerdo al jugador:
        También compruebo si el jugador hizo chinchón: */
        setJugadorEnPantalla();
    }

    private void setJugadorEnPantalla() {

        Jugador jugador = jugadores.get(jugadorActual);
        // Cambio el cuadro del nombre:
        nombreTV.setText(getString(R.string.ac_nombre, jugador.getNombre(), jugador.getPuntos()));

        //Indico que éste jugador fue el que cortó:
        if (cortador == jugadorActual) {
            corteTV.setText(R.string.ac_corte);
        } else {
            corteTV.setText("");
        }

        if (jugador.getMano().esChinchon() && (jugadorActual == cortador)) {
            // Si el jugador tiene chinchón entonces termina el juego:
            Intent intent = new Intent(AcomodarActivity.this, GanadorActivity.class);
            intent.putExtra(Constantes.INTENT_JUGADORES, jugadores);
            intent.putExtra(Constantes.INTENT_GANADOR, jugadorActual + 1);
            intent.putExtra(Constantes.INTENT_CHINCHON, true);
            startActivity(intent);
        } else {
            // Muestro la mano en pantalla:
            jugador.getMano().toTableLayout(tablaMano, false);

            // Pongo todas las banderas de las cartas en deseleccionado:
            for (int carta = 0; carta < 7; carta++) {
                estados[carta] = EST_DESELECCIONADO;
                filaBotones.getChildAt(carta).setBackgroundColor(clr_des);
            }
        }
        // Limpio el mensaje de error:
        errorTV.setText("");
    }
}
