package juego.chinchon;

import android.content.res.Resources;
import android.widget.ImageView;
import java.util.ArrayList;
import java.util.Random;

/**
 * Clase para representar un Mazo de cartas españolas, de 50 cartas. 12 cartas
 * para los cuatro palos más dos comodines.
 * También se utiliza para representar al pozo de descarte, al cual se le
 * colocan las cartas que descarta cada jugador durante el transcurso del juego.
 * @author LeoAM
 */
public class Mazo {
    private static final int MAXCartas = 50;
    private final ArrayList<Carta> cartas;
    private int cantidad;

    /**
     * Constructor de la clase Mazo. Puede crear el mazo vacío o con las cartas.
     * @param vacio Indica si hay que construir el mazo vacío (para el pozo de
     * descarte) o lleno (para el mazo principal).
     */
    public Mazo(boolean vacio) {
        cartas = new ArrayList<>();
        if (vacio) {
            cantidad = 0;
        } else {
            cantidad = MAXCartas;
            setCartas();
            mezclar();
        }
    }

    // Método privado para crear las cartas para el mazo.
    private void setCartas() {
        for (Palo p : Palo.values()) {
            if (p == Palo.Comodin) {
                break;
            }
            for (int j = 1; j <= 12; j++) {
                cartas.add(new Carta(j, p));
            }
        }
        cartas.add(new Carta(0, Palo.Comodin));
        cartas.add(new Carta(0, Palo.Comodin));
    }

    // Método privado para mezclar el mazo.
    private void mezclar() {
        if (cantidad > 1) {
            Random rdm = new Random();
            Carta tmp;
            int j;
            for (int i = cantidad - 1; i > 0; i--) {
                j = rdm.nextInt(i);
                tmp = cartas.get(i);
                cartas.set(i, cartas.get(j));
                cartas.set(j, tmp);
            }
        }
    }

    /**
     * Roba una carta del mazo.
     * @return La carta robada.
     */
    public Carta robar() {
        cantidad--;
        return cartas.remove(0);
    }

    /**
     * Muestra cuál es la carta en el tope del mazo. No la quita.
     * @return La carta en el tope.
     */
    public Carta tope() {
        if(cartas.isEmpty()) {
            return null;
        } else {
            return cartas.get(0);
        }
    }

    /**
     * Coloca una carta al tope del mazo.
     * @param c La carta a colocar.
     */
    public void colocar(Carta c) {
        if (cantidad < MAXCartas) {
            cartas.add(0, c);
            cantidad++;
        }
    }

    /**
     * Vuelca las cartas de un mazo en éste. Luego las mezcla.
     * @param m El mazo de donde se sacan las cartas.
     */
    public void volcar(Mazo m) {
        this.cantidad = m.cantidad;
        m.cantidad = 0;
        for (int i = 0; i < this.cantidad; i++) {
            this.cartas.add(m.cartas.remove(0));
        }
        this.mezclar();
    }

    /**
     * Reparte las cartas entre los jugadores. Reparte 8 cartas para el primer
     * jugador y 7 para el segundo.
     * @param jugadores Lista de jugadores.
     */
    public void repartir(ArrayList<Jugador> jugadores) {
        // Se reparte solo al inicio de la partida.
        if (cantidad == MAXCartas) {
            int cantJugadores = jugadores.size(), j;

            // Vacío las manos de los jugadores:
            for (Jugador jug : jugadores) {
                jug.vaciarMano();
            }

            // Ciclo de cartas
            for (int i = 0; i < 7; i++) {
                for (j = 0; j < cantJugadores; j++) {
                    jugadores.get(j).getMano().addCarta(this.robar());
                }
            }

            // Carta extra para el primer jugador de la ronda:
            jugadores.get(0).getMano().addCarta(this.robar());
        }
    }

    @Override
    public String toString() {
        String str = "";
        for (int i = 0; i < cantidad; i++) {
            str = str.concat(cartas.get(i).toString() + "\n");
        }
        return str;
    }

    /**
     * Devuelve la cantidad de cartas en el mazo.
     * @return Devuelve la cantidad de cartas en el mazo.
     */
    public int getCantidad() {
        return cantidad;
    }

    /**
     * Dada un componente de imagen, le coloca la imagen del tope del mazo.
     * @param iv Componente de ImageView.
     * @param oculto Si se trata del mazo de robo, entonces muestra el dorso de
     * la carta en lugar de la imagén de la carta en particular.
     */
    public void setImagenTope(ImageView iv, boolean oculto) {
        Resources res = iv.getResources();
        if (cartas.isEmpty()) {
            /* Si el mazo o la pila están vacíos, entonces dejó vacío el espacio
            para la imagen. */
            iv.setImageResource(res.getIdentifier("vacio", "drawable", "juego.chinchon"));
        } else
        if (oculto) {
            iv.setImageResource(res.getIdentifier("dorso", "drawable", "juego.chinchon"));
        } else {
            iv.setImageResource(res.getIdentifier(cartas.get(0).getImagePath(), "drawable", "juego.chinchon"));
        }
    }

    /**
     * Comprueba que el mazó esté vacío.
     * @return Si el mazo está vacío o no.
     */
    public boolean vacio() {
        return cartas.isEmpty();
    }
}
