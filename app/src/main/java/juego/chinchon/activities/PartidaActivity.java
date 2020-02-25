package juego.chinchon.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import juego.chinchon.Jugador;
import juego.chinchon.Mazo;
import juego.chinchon.Carta;
import juego.chinchon.Constantes;
import java.util.ArrayList;

import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.ImageView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import com.example.leoam.chinchonandroid.R;

/**
 *
 * @author LeoAM
 */
public class PartidaActivity extends AppCompatActivity {

    private final View.OnClickListener cartaClickListener;
    private final View.OnClickListener mazoClickListener;
    private final View.OnClickListener pilaClickListener;
    private final View.OnClickListener cortarClickListener;

    // Estados del turno:
    private static final int FASETURNO_ROBARCARTA = 0;
    private static final int FASETURNO_TIRARCARTA = 1;

    // Inicio de la cuenta de turnos:
    private static final int TURNO_INICIAL = 1;

    // Índice nulo para selección de cartas:
    private static final int CARTA_NOSELECT = 0;

    // Código de pedido para startActivityForResult:
    private static final int RC_CORTE = 1;

    private int fase;                   // Fase del turno.
    private int carta = CARTA_NOSELECT; // Carta seleccionada.
    private int numJugador;             // Índice del jugador actual.
    private int jugadorInicial = 0;     // Índice del jugador que inicia la ronda.
    private int numTurno = TURNO_INICIAL;

    private ArrayList<Jugador> jugadores; // Lista de jugadores.
    private Mazo mazo, pila;

    // Carta usada para ser cortada. Puede ser recuperada si hubo un mal corte.
    Carta cartaCorte;

    // Componentes en pantalla:
    private final ArrayList<TableLayout> tablas; // Lista de tablas en pantalla.
    private ImageView iv_mazo, iv_pila;
    private TextView tv_puntos_1;
    private TextView tv_puntos_2;
    private TextView tv_carta;
    private TextView tv_turno;

    public PartidaActivity() {

        this.cartaClickListener = new View.OnClickListener() {

            public void onClick(View v) {
                int estaCarta = Integer.parseInt((String) v.getTag());
                if (!(estaCarta == 8 & fase == FASETURNO_ROBARCARTA)) {
                    if (carta == CARTA_NOSELECT) {
                        carta = estaCarta;
                        // Muestro el nombre de la carta:
                        tv_carta.setText(jugadores.get(numJugador).getMano().getCarta(carta).toString());
                    }
                    else {
                        // Intercambio las dos cartas seleccionadas:
                        jugadores.get(numJugador).getMano().swapCartas(carta, estaCarta);
                        // Actualizo la tabla con las cartas intercambiadas:
                        jugadores.get(numJugador).getMano().toTableLayout(tablas.get(numJugador), fase == FASETURNO_TIRARCARTA);
                        // Limpio la selección de cartas:
                        carta = CARTA_NOSELECT;
                        tv_carta.setText("");
                    }
                }
            }

        };

        this.mazoClickListener = new View.OnClickListener() {

            public void onClick(View v) {
                switch (fase) {
                    case FASETURNO_ROBARCARTA:
                        /* Si el mazo está vacío, vuelco la pila para seguir
                           repartiendo: */
                        if (mazo.getCantidad() == 0) {
                            mazo.volcar(pila);
                            mazo.setImagenTope(iv_mazo, true);
                            pila.setImagenTope(iv_pila, false);
                        }
                        jugadores.get(numJugador).getMano().addCarta(mazo.robar());
                        fase = FASETURNO_TIRARCARTA;
                        jugadores.get(numJugador).getMano().toTableLayout(tablas.get(numJugador), true);

                        /* Si el mazo se vacía luego de robar una carta, cambio
                           su imagen para que se vea que está vacío: */
                        if (mazo.getCantidad() == 0) {
                            mazo.setImagenTope(iv_mazo, true);
                        }
                        carta = CARTA_NOSELECT;
                        tv_carta.setText("");
                        break;
                    case FASETURNO_TIRARCARTA:
                        /* Para renunciar hay que hacer click sobre el mazo
                        fuera de la fase de robo. */
                        //<editor-fold defaultstate="collapsed" desc="AlertDialog.Builder builder">
                        // Diálogo de confirmación para la renuncia de un jugador:
                        AlertDialog.Builder builder = new AlertDialog.Builder(PartidaActivity.this);

                        builder
                                .setMessage("¿Desea renunciar?")
                                .setPositiveButton("Si", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        int ganador;
                                        if (numJugador == 0) {
                                            ganador = Constantes.GANADOR_2;
                                        } else {
                                            ganador = Constantes.GANADOR_1;
                                        }
                                        Intent i = new Intent(PartidaActivity.this, GanadorActivity.class);
                                        i.putExtra(Constantes.INTENT_JUGADORES, jugadores);
                                        i.putExtra(Constantes.INTENT_GANADOR, ganador);
                                        finish();
                                        startActivity(i);
                                    }

                                })
                                .setNegativeButton("No", null);
                        //</editor-fold>
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        break;
                    default:
                }
            }

        };

        this.pilaClickListener = new View.OnClickListener() {

            public void onClick(View v) {
                switch (fase) {
                    case FASETURNO_ROBARCARTA:
                        if (!pila.vacio()) {
                            jugadores.get(numJugador).getMano().addCarta(pila.robar());
                            fase = FASETURNO_TIRARCARTA;
                            jugadores.get(numJugador).getMano().toTableLayout(tablas.get(numJugador), true);
                            carta = 0;
                            pila.setImagenTope(iv_pila, false);
                        }
                        break;
                    case FASETURNO_TIRARCARTA:
                        if (carta != CARTA_NOSELECT) {
                            pila.colocar(jugadores.get(numJugador).getMano().tirarCarta(carta));
                            carta = CARTA_NOSELECT;
                            tv_carta.setText("");
                            cambioTurno();
                        }
                        break;
                    default:
                }
            }

        };

        this.cortarClickListener = new View.OnClickListener() {

            public void onClick(View v) {
                if (fase == FASETURNO_TIRARCARTA && numTurno > TURNO_INICIAL + 1) {
                    //Compruebo que el jugador eligió la carta para cortar:
                    if (carta != CARTA_NOSELECT) {
                        // Tiro la carta con la que se cortó a la pila:
                        cartaCorte = jugadores.get(numJugador).getMano().tirarCarta(carta);

                        // Voy a la pantalla de corte:
                        Intent i = new Intent(PartidaActivity.this, AcomodarActivity.class);
                        i.putExtra(Constantes.INTENT_CORTE, numJugador);
                        i.putExtra(Constantes.INTENT_JUGADORES, jugadores);
                        startActivityForResult(i, RC_CORTE);
                    }
                }
            }

        };
        jugadores = new ArrayList<>();
        tablas = new ArrayList<>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.mesajuego);

        /* Obtengo del layout los controles para manejar las cartas de los
           jugadores: */
        tablas.add((TableLayout) findViewById(R.id.mj_mano_1));
        tablas.add((TableLayout) findViewById(R.id.mj_mano_2));
        for (TableLayout t : tablas) {
            setClickListeners(t);
        }

        //Inicializo los mazos
        mazo = new Mazo(false);
        pila = new Mazo(true);

        // Inicializo los jugadores:

        // Obtengo los jugadores de "RegistroJugadoresActivity":
        jugadores = (ArrayList<Jugador>) getIntent().getSerializableExtra(Constantes.INTENT_JUGADORES);

        mazo.repartir(jugadores);

        // Inicializo los gráficos de las cartas:
        for (int i = 0; i < jugadores.size(); i++) {
            jugadores.get(i).getMano().toTableLayout(tablas.get(i), false);
        }

        /* Inicializo los cuadros de texto con los nombres de los jugadores y
           sus puntajes: */
        TextView tv_nombre_1 = findViewById(R.id.mj_nombrejugador_1);
        tv_nombre_1.setText(jugadores.get(0).getNombre());
        tv_puntos_1 = findViewById(R.id.mj_puntos_1);
        tv_puntos_1.setText(getString(R.string.mj_puntos, jugadores.get(0).getPuntos()));

        TextView tv_nombre_2 = findViewById(R.id.mj_nombrejugador_2);
        tv_nombre_2.setText(jugadores.get(1).getNombre());
        tv_puntos_2 = findViewById(R.id.mj_puntos_2);
        tv_puntos_2.setText(getString(R.string.mj_puntos, jugadores.get(1).getPuntos()));


        // Inicializo los controles gráficos del mazo y de la pila en pantalla:
        iv_mazo = findViewById(R.id.mj_mazo);
        iv_mazo.setOnClickListener(mazoClickListener);

        iv_pila = findViewById(R.id.mj_pila);
        iv_pila.setOnClickListener(pilaClickListener);

        pila.setImagenTope(iv_pila, false);

        // Inicializo el botón para cortar:
        Button btn_cortar = findViewById(R.id.mj_cortar_btn);
        btn_cortar.setOnClickListener(cortarClickListener);

        tv_carta = findViewById(R.id.mj_nombrecarta);
        tv_turno = findViewById(R.id.mj_numturno);
        tv_turno.setText(getString(R.string.mj_turno, TURNO_INICIAL));

        numJugador = jugadorInicial;
    }

    private void setClickListeners(TableLayout tabla) {
        for (int i = 0; i < 2; i++) {
            // En este ciclo obtengo la referencia a cada fila "TableRow" de la tabla.
            TableRow tr = (TableRow) tabla.getChildAt(i);
            for (int j = 0; j < 4; j++) {
                /* En el ciclo interno coloco como ClickListener de las imagenes
                   a "cartaClickListener", definido más arriba. */
                tr.getChildAt(j).setOnClickListener(cartaClickListener);
            }
        }
    }

    private void cambioTurno() {
        // Cambio del jugador actual:
        numJugador = (numJugador + 1) % jugadores.size();

        // Cambio de fase:
        fase = FASETURNO_ROBARCARTA;

        // Inicio la actividad para cambiar de turno:
        // Se inicia antes de actualizar la mesa de juego.
        Intent intent = new Intent(PartidaActivity.this, CambioTurnoActivity.class);
        intent.putExtra(Constantes.INTENT_CARTA, pila.tope());
        intent.putExtra(Constantes.INTENT_JUGADOR, jugadores.get(numJugador));
        startActivity(intent);

        // Cambio de la mano en pantalla:
        if (numJugador == 0) {
            tablas.get(0).setVisibility(TableLayout.VISIBLE);
            tablas.get(1).setVisibility(TableLayout.GONE);
        } else {
            tablas.get(1).setVisibility(TableLayout.VISIBLE);
            tablas.get(0).setVisibility(TableLayout.GONE);
        }

        jugadores.get(numJugador).getMano().toTableLayout(tablas.get(numJugador), false);

        /* Si el mazo quedó vacío entonces vuelco la pila en el mazo para seguir
           jugando. */
        if (mazo.vacio()) {
            mazo.volcar(pila);
        }

        // Cambio de la imagen de la pila:
        pila.setImagenTope(iv_pila, false);

        numTurno++;
        tv_turno.setText(getString(R.string.mj_turno, numTurno));

        /*
        // Inicio la actividad para cambiar de turno:
        Intent i = new Intent(PartidaActivity.this, CambioTurnoActivity.class);
        i.putExtra(Constantes.INTENT_CARTA, pila.tope());
        i.putExtra(Constantes.INTENT_JUGADOR, jugadores.get(numJugador));
        startActivity(i);
        */
    }

    private void cambioRonda() {

        jugadorInicial = 1 - jugadorInicial;
        numJugador = jugadorInicial;

        // Actualizo los cuadros de texto con los puntajes:
        tv_puntos_1.setText(getString(R.string.mj_puntos, jugadores.get(0).getPuntos()));
        tv_puntos_2.setText(getString(R.string.mj_puntos, jugadores.get(1).getPuntos()));

        // Inicializo el mazo y la pila:
        mazo = new Mazo(false);
        pila = new Mazo(true);

        // Cambio las imágenes del mazo y de la pila:
        mazo.setImagenTope(iv_mazo, true);
        pila.setImagenTope(iv_pila, false);

        // Reparto las cartas:
        mazo.repartir(jugadores);

        // Muestro la mano de los jugadores en las tablas.
        // Muestro la mano del jugador que comienza, y oculto la del otro:
        int segJugador = 1 - numJugador;

        jugadores.get(numJugador).getMano().toTableLayout(tablas.get(numJugador), false);
        tablas.get(numJugador).setVisibility(TableLayout.VISIBLE);

        jugadores.get(segJugador).getMano().toTableLayout(tablas.get(segJugador), false);
        tablas.get(segJugador).setVisibility(TableLayout.GONE);

        //Limpio los controles de la mesa e inicio la fase de robo:
        fase = FASETURNO_ROBARCARTA;
        numTurno = TURNO_INICIAL;
        tv_turno.setText(getString(R.string.mj_turno, TURNO_INICIAL));
        carta = CARTA_NOSELECT;
        tv_carta.setText("");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_CORTE) {
            switch (resultCode) {
                case 1: // Se hizo la acomodación de cartas efectivamente:
                    jugadores = (ArrayList<Jugador>) data.getSerializableExtra(Constantes.INTENT_JUGADORES);

                    // Analizo el estado de los dos jugadores:
                    boolean v1 = jugadores.get(0).estaVencido();
                    boolean v2 = jugadores.get(1).estaVencido();

                    /* Se concluye la partida, porque uno o los dos jugadores
                       perdieron: */
                    if (v1 || v2) {
                        int ganador = Constantes.EMPATE;

                        if (v1 ^ v2) {
                            ganador = (v2)? Constantes.GANADOR_1 : Constantes.GANADOR_2;
                        }

                        // Paso a la actividad que muestra al ganador:
                        Intent intent = new Intent(PartidaActivity.this, GanadorActivity.class);
                        intent.putExtra(Constantes.INTENT_JUGADORES, jugadores);
                        intent.putExtra(Constantes.INTENT_GANADOR, ganador);
                        intent.putExtra(Constantes.INTENT_CHINCHON, false);
                        finish();
                        startActivity(intent);
                    }

                    // Cambio de ronda:
                    cambioRonda();
                    break;
                case 2: // Se cortó con más de 5 puntos por lo tanto se reaunuda la partida.
                    // Agrego la carta de vuelta a la mano:
                    jugadores.get(numJugador).getMano().addCarta(cartaCorte);

                    jugadores.get(numJugador).getMano().toTableLayout(tablas.get(numJugador), true);

                    carta = CARTA_NOSELECT;
                    tv_carta.setText("");

                    tv_turno.setText(R.string.mj_malcorte);
                    break;
                default:
            }
        }
    }
}
