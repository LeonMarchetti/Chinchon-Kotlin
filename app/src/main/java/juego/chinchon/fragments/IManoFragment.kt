package juego.chinchon.fragments

interface IManoFragment {
    /**
     * Acción que realiza la actividad cuando se selecciona una carta en el
     * fragmento.
     *
     * @param i Índice de la carta seleccionada.
     */
    fun seleccionarCarta(i: Int)
}