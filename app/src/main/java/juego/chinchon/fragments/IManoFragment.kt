package juego.chinchon.fragments

interface IManoFragment {
    /** Arrastra una carta hacia otra. */
    fun arrastrarCarta(origen: Int, destino: Int)
}