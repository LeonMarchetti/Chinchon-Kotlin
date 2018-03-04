package juego.chinchon;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Clase Carta, representa una carta, de la baraja española.
 * @author LeoAM
 */
public class Carta implements Comparable<Carta>, Serializable {

    private static final int VALORCOMODIN = 25;

    private final int valor;
    private final Palo palo;
    private final String imagenPath;

    /**
     * Constructor para la clase Carta, pasando valor y palo.
     * @param v Valor de la carta a crear.
     * @param p Palo de la carta.
     */
    Carta(int v, Palo p) {
        if (v >= 1 && v <= 12) {
            this.valor = v;
        } else {
            //Comodín:
            this.valor = VALORCOMODIN;
        }
        this.palo = p;

        if (palo == Palo.Comodin) {
            imagenPath = this.palo.getPaloPath();
        } else {
            imagenPath = this.palo.getPaloPath() + "_" + this.valor + "s";
        }
    }

    /**
     * Constructor de la clase Carta, pasando otra carta como parámetro, el cual
     * el nuevo objeto tomará el mismo valor numérico y palo.
     * @param c Carta a copiar.
     */
    Carta(Carta c) {
        this.valor = c.valor;
        this.palo = c.palo;
        this.imagenPath = c.imagenPath;
    }

    /**
     * Devuelve el valor numérico de la carta.
     * @return El valor numérico de la carta.
     */
    int getValor() {
        return this.valor;
    }

    /**
     * Devuelve el "palo" de la carta (Espada, Basto, Oro o Copa)
     * @return El palo de la carta.
     */
    Palo getPalo() {
        return this.palo;
    }

    @Override
    public String toString() {
        return valor + " de " + palo.toString();
    }

    @Override
    public int compareTo(@NonNull Carta c) {
        return this.valor - c.valor;
    }

    /**
     * Método que devuelve el nombre del archivo que almacena la imagen para
     * esta carta. Se genera cuando se crea el objeto.
     * @return El nombre del archivo de la imagen de esta carta.
     */
    public String getImagePath() {
        return imagenPath;
    }
}