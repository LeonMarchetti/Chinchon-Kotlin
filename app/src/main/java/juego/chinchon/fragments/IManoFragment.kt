package juego.chinchon.fragments

interface IManoFragment {
    /** Arrastra una carta hacia otra. */
    fun arrastrarCarta(origen: Int, destino: Int)

    /**
     * Acción que realiza la actividad cuando se selecciona una carta en el
     * fragmento.
     *
     * @param i Índice de la carta seleccionada.
     */
    fun seleccionarCarta(i: Int)
}