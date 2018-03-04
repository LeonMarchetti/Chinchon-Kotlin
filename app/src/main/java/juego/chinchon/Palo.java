package juego.chinchon;

/**
 * Clase que enumera los palos que puede tener una carta: Espada, Basto, Oro,
 * Copa o Comodín.
 * @author LeoAM
 */
public enum Palo {
    Espada,
    Basto,
    Oro,
    Copa,
    Comodin;

    @Override
    public String toString() {
        switch (this) {
            case Espada:
                return "Espada";
            case Basto:
                return "Basto";
            case Oro:
                return "Oro";
            case Copa:
                return "Copa";
            case Comodin:
                return "Comodín";
            default:
        }
        return "";
    }

    /**
     * Método que devuelve la parte del nombre del archivo de la carta que
     * contiene el palo. El nombre del archivo se conforma por:
     * [getPalopath() + "_" + Carta.valor + "s"]
     *
     * Por ejemplo: "espadas_10s"
     * @return Palo de la carta.
     */
    public String getPaloPath() {
        switch(this) {
            case Espada:
                return "espadas";
            case Basto:
                return "bastos";
            case Oro:
                return "oros";
            case Copa:
                return "copas";
            case Comodin:
                return "joker";
            default:
        }
        return "";
    }
}
