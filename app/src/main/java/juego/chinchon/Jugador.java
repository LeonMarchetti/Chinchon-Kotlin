package juego.chinchon;

import java.io.Serializable;

/**
 * Clase Jugador, representa a un jugador que participa en el juego.
 * @author LeoAM
 */
public class Jugador implements Serializable {

    // Máxima cantidad de puntos que un jugador puede tener:
    private static final int MAX_PUNTOS = 100;
    private final String nombre;
    private final Mano mano;
    private int puntaje;

    /**
     * Constructor de la clase Jugador
     * @param n Nombre del jugador.
     */
    public Jugador(String n) {
        nombre = n;
        puntaje = 0;
        mano = new Mano();
    }

    /**
     * Devuelve el nombre del jugador.
     * @return El nombre del jugador.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Devuelve las cartas (la mano) del jugador
     * @return La mano con las cartas del jugador.
     */
    public Mano getMano() {
        return mano;
    }

    void vaciarMano() {
        this.mano.vaciar();
    }

    /**
     * Devuelve el puntaje del jugador.
     * @return El puntaje del jugador.
     */
    public int getPuntos() {
        return puntaje;
    }

    /**
     * Suma los puntos dados al puntaje del jugador.
     * @param n Los puntos a adicionar.
     */
    public void addPuntos(int n) {
        puntaje += n;
    }

    /**
     * Resta 10 puntos del jugador. Sucede cuando un jugador corta y se queda
     * sin cartas sin acomodar.
     */
    public void restar10() {
        puntaje -= 10;
    }

    /**
     * Método que comprueba si un jugador fue vencido, específicamente si el
     * jugador superó los 100 puntos, que se considera fuera del juego.
     * @return True, si el jugador fue vencido.
     */
    public boolean estaVencido() {
        return (puntaje > MAX_PUNTOS);
    }
}
