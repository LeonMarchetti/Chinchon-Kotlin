package juego.chinchon;

import android.content.res.Resources;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Clase Mano, representa el conjunto de cartas en "la mano" del jugador.
 * Puede contener hasta 8 cartas, en la cual la octava carta solo se tiene
 * durante el propio turno.
 * @author LeoAM
 */
public class Mano implements Serializable {

    private final ArrayList<Carta> cartas;
    private Carta cartaExtra;

    /**
     * Constructor de la clase Mano.
     */
    Mano() {
        cartas = new ArrayList<>(7);
    }

    /**
     * Agrega una carta a la mano. Si es que la mano tiene menos de 7 cartas
     * (durante el reparto inicial) entonces la agrega directamente. Si tiene ya
     * las 7 cartas, entonces se agrega como una carta "extra".
     * @param c La carta a agregar a la mano.
     */
    public void addCarta(Carta c) {
        if (c != null) {
            if (cartas.size() >= 7) {
                //Si la mano está llena (pleno juego) lo asigno a la carta extra.
                cartaExtra = new Carta(c);
                return;
            } else {
                cartas.add(c);
            }
        }
        cartas.size();
    }

    /**
     * Descarta una carta de la mano, dada la posición de la carta en la mano.
     * La carta seleccionada se coloca en la posición de carta extra, y luego
     * se devuelve.
     * @param n La posición en la mano (1-8)
     * @return La carta a descartar
     */
    public Carta tirarCarta(int n) {
        n--;
        if (esIndice(n)) {
            if ( n != 7 ) { //Intercambio la carta a tirar con la carta extra
                Carta tmp = cartas.get(n);
                cartas.set(n, cartaExtra);
                cartaExtra = tmp;
            }
            return cartaExtra;
        } else {
            return null;
        }
    }

    /**
     * Devuelve la carta dada su posición en la mano.
     * @param n Índice de la carta.
     * @return La carta seleccionada.
     */
    public Carta getCarta(int n) {
        n--;
        if (esIndice(n)) {
            if (n != 7) {
                return cartas.get(n);
            } else {
                return cartaExtra;
            }
        } else {
            return null;
        }
    }

    /**
     * Intercambia de lugar dos cartas, dadas sus posiciones.
     * @param i Posición de la primera carta. (1-8)
     * @param j Posición de la segunda carta. (1-8)
     */
    public void swapCartas(int i, int j) {
        if (i != j) {
            int max = (i > j)? i : j;
            int min = (max == i)? j : i;
            max--;
            min--;
            if (esIndice(max) && (esIndice(min))) {
                Carta tmp = cartas.get(min);
                if (max == 7) {
                    cartas.set(min, cartaExtra);
                    cartaExtra = tmp;
                } else {
                    cartas.set(min, cartas.get(max));
                    cartas.set(max, tmp);
                }
            }
        }
    }

    /**
     * Indica si las cartas dadas (sus posiciones en la mano) tienen el mismo
     * valor numérico, con lo cual forman un juego. Son necesarias 3 cartas
     * como mínimo para el juego, y 4 como máximo.
     * @param indices Las posiciones de las cartas en la mano.
     * @return Si forman el juego de cartas con el mismo valor numérico.
     */
    public boolean mismoValor(int[] indices) {
        if (indices.length == 3 || indices.length == 4) {
            boolean hayComodin = false;
            int valor = 0; //Valor en común entre todas las cartas para formar
            // el juego.
            Carta c;
            for (int indice : indices) {

                c = cartas.get(indice);

                // La carta es un comodín?:
                if (c.getPalo() == Palo.Comodin) {
                    if (hayComodin) { // Solo se permite un (1) comodín por juego.
                        return false;
                    }
                    hayComodin = true;
                } else {
                    if (valor == 0) { // Pongo el valor de la primer carta (no
                        // comodín) que encuentre.
                        valor = c.getValor();
                    } else {
                        if (c.getValor() != valor) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Indica si las cartas dadas (sus posiciones en la mano) tienen el mismo
     * palo, con lo cual forman un juego. Son necesarias 3 cartas como mínimo
     * para el juego.
     * @param indices Las posiciones de las cartas en la mano.
     * @return Si forman el juego de cartas con el mismo palo.
     */
    public boolean mismoPalo(int[] indices) {

        if (indices.length > 2 && indices.length < 8) {

            //Agrego las cartas seleccionadas en un arreglo y los ordeno:
            ArrayList<Carta> tmp = new ArrayList<>(indices.length);
            Carta c;
            boolean hayComodin = false;

            for (int indice : indices) {

                c = cartas.get(indice);

                //Filtro los comodines, si hay más de uno devuelvo falso:
                if (c.getPalo() == Palo.Comodin) {
                    if (hayComodin) {
                        return false;
                    }
                    hayComodin = true;
                } else {
                    tmp.add(c);
                }
            }
            Collections.sort(tmp);

            c = tmp.get(0);
            // Palo en común que deben tener todas las cartas del juego:
            Palo paloJuego = c.getPalo();
            int valorAnt = c.getValor(),    // Valor de la carta anterior
                    tmpSize = tmp.size();

            boolean aplicaComodin = hayComodin;

            for (int i = 1; i < tmpSize; i++) {
                c = tmp.get(i);
                if (c.getPalo() != paloJuego) {
                    // Si el palo es distinto no hay juego
                    return false;
                }

                /*
                    Si el valor de esta carta no es consecutivo a la anterior no
                    hay juego. Si hay un comodin entonces se puede permitir que
                    éste reemplaze a una carta que esté en el medio entre dos
                    cartas.
                */
                if (c.getValor() != valorAnt + 1) {
                    if (aplicaComodin && (c.getValor() == valorAnt + 2)) {
                        /* "aplicaComodin" cambia a falso, ya que un comodín
                        solo puede reemplazar a una sola carta. */
                        aplicaComodin = false;
                    } else {
                        return false;
                    }
                }
                valorAnt = c.getValor();
            }
            return true;
        }
        return false;
    }

    /**
     * Indica si las cartas de la mano formán chinchón. Se forma chinchón cuando
     * las 7 cartas son del mismo palo y además tienen valores numéricos
     * consecutivos. Ejemplo: 1, 2, 3, 4, 5, 6, 7 de Oro.
     * @return Si hay chinchón en la mano,
     */
    public boolean esChinchon() {
        ArrayList<Carta> tmp = new ArrayList<>(cartas);
        Collections.sort(tmp); //Ordeno las cartas.

        Palo p = cartas.get(0).getPalo(); //El palo de la primera carta debería
        //ser el mismo para todas las caratas.

        for (int i = 1; i < tmp.size(); i++) {
            if ( ( p != tmp.get(i).getPalo() ) ||
                    ( tmp.get(i).getValor() != ( tmp.get(i-1).getValor() + 1 ) ) )
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Calcula la cantidad de puntos acumulados por las cartas que no pudieron
     * ser acomodadas.
     * @param acomodadas arreglo de bits, uno por carta, que indican cuál fue
     * acomodada en un juego y cuál no.
     * @return La cantidad de puntos a sumar al jugador.
     */
    public int getPuntos(boolean[] acomodadas) {
        int puntos = 0;
        if (acomodadas.length >= 7) {
            for (int i = 0; i < 7; i++) {
                if (!acomodadas[i]) {
                    puntos += cartas.get(i).getValor();
                }
            }
        }
        return puntos;
    }

    private boolean esIndice(int n) {
        return n >= 0 && n <= 7;
    }

    /**
     * Coloca en una TableLayout las cartas en la mano del jugador, 4 por fila,
     * para una tabla de 2 filas y 4 columnas.
     * @param tabla La tabla donde almacenar las imágenes de las cartas.
     * @param octavaCarta Indica si se tiene que mostrar la octava carta de la
     * mano.
     */
    public void toTableLayout(TableLayout tabla, boolean octavaCarta) {

        Resources res = tabla.getResources();
        TableRow tr;
        View v;
        Carta c;

        if (tabla.getChildCount() != 2) {
            return;
        }

        // Armo la primera fila de la tabla (las cuatro primeras cartas):
        tr = (TableRow) tabla.getChildAt(0);
        if (tr.getChildCount() != 4 ) {
            return;
        }
        for (int i = 0; i < 4; i++) {
            c = cartas.get(i);
            v = tr.getChildAt(i);
            if (v instanceof ImageView) {
                ((ImageView)v).setImageResource(res.getIdentifier(c.getImagePath(), "drawable", "juego.chinchon"));
            } else {
                return;
            }
        }

        // Armo la segunda fila de la tabla (las tres o cuatro últimas cartas):
        tr = (TableRow) tabla.getChildAt(1);
        if (tr.getChildCount() != 4 ) {
            return;
        }
        for (int i = 0; i < 3; i++) {
            c = cartas.get(i + 4);
            v = tr.getChildAt(i);
            if (v instanceof ImageView) {
                ((ImageView)v).setImageResource(res.getIdentifier(c.getImagePath(), "drawable", "juego.chinchon"));
            } else {
                return;
            }
        }

        /* Muestro la última carta de la mano. Al principio del turno, el
           jugador solo tiene 7 cartas, por lo tanto no se muestra. Cuando roba
           una carta, se muestran las 8 cartas. */
        v = tr.getChildAt(3);
        if (v instanceof ImageView) {
            if (octavaCarta) {
                // Se muestra la octava carta:
                ((ImageView)v).setImageResource(res.getIdentifier(cartaExtra.getImagePath(), "drawable", "juego.chinchon"));
            }
            else {
                // No se muestra la octava carta:
                ((ImageView)v).setImageResource(0); //Dejo vacía la imágen.
            }
        }
    }

    /**
     * Éste método vacía el contenido de la mano.
     */
    void vaciar() {
        cartas.clear();
        cartaExtra = null;
    }
}
