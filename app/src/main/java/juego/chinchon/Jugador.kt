package juego.chinchon

import android.os.Parcel
import android.os.Parcelable

/**
 * Clase Jugador, representa a un jugador que participa en el juego.
 * @author LeoAM
 */
class Jugador(val nombre: String) : Parcelable {

    /**
     * @param nombre Nombre del jugador
     * @param puntos Puntos iniciales del jugador
     */
    constructor(nombre: String, puntos: Int) : this(nombre) {
        this.puntos = puntos
    }

    /**
     * Devuelve las cartas (la mano) del jugador
     * @return La mano con las cartas del jugador.
     */
    var mano: Mano = Mano()

    /**
     * Devuelve el puntaje del jugador.
     * @return El puntaje del jugador.
     */
    var puntos = 0
        private set

    fun vaciarMano() {
        mano.vaciar()
    }

    /**
     * Suma los puntos dados al puntaje del jugador.
     * @param n Los puntos a adicionar.
     */
    fun addPuntos(n: Int) {
        puntos += n
    }

    /**
     * Resta 10 puntos del jugador. Sucede cuando un jugador corta y se queda
     * sin cartas sin acomodar.
     */
    fun restar10() {
        puntos -= 10
    }

    /**
     * Método que comprueba si un jugador fue vencido, específicamente si el
     * jugador superó los 100 puntos, que se considera fuera del juego.
     * @return True, si el jugador fue vencido.
     */
    fun estaVencido(): Boolean {
        return puntos > MAX_PUNTOS
    }

    override fun toString(): String {
        return "$nombre ($puntos puntos)"
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of [.writeToParcel],
     * the return value of this method must include the
     * [.CONTENTS_FILE_DESCRIPTOR] bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     */
    override fun describeContents(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Jugador

        return this.nombre == other.nombre
    }

    constructor(parcel: Parcel) : this(parcel.readString()!!, parcel.readInt()) {
        mano = parcel.readParcelable(Mano::class.java.classLoader)!!
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param parcel The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     * May be 0 or [.PARCELABLE_WRITE_RETURN_VALUE].
     */
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(nombre)
        parcel.writeInt(puntos)
        parcel.writeParcelable(mano, flags)
    }

    override fun hashCode(): Int {
        return nombre.hashCode()
    }

    companion object CREATOR : Parcelable.Creator<Jugador> {
        override fun createFromParcel(parcel: Parcel): Jugador {
            return Jugador(parcel)
        }

        override fun newArray(size: Int): Array<Jugador?> {
            return arrayOfNulls(size)
        }

        /** Máxima cantidad de puntos que un jugador puede tener: */
        private const val MAX_PUNTOS: Int = 100
    }
}