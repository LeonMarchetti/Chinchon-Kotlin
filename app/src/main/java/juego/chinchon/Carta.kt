package juego.chinchon

import android.os.Parcel
import android.os.Parcelable

/**
 * Clase Carta, representa una carta, de la baraja española.
 *
 * @property valor Valor de la carta a crear.
 * @property palo Palo de la carta.
 * @author LeoAM
 */
class Carta(val valor: Int, val palo: Palo): Comparable<Carta>, Parcelable {
    /**
     * Método que devuelve el nombre del archivo que almacena la imagen para
     * esta carta. Se genera cuando se crea el objeto.
     * @return El nombre del archivo de la imagen de esta carta.
     */
    val imagePath: String

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readSerializable() as Palo
    )

    init {
        imagePath = if (palo == Palo.Comodin) { palo.paloPath } else { palo.paloPath + "_" + valor + "s" }
    }

    override fun toString(): String {
        if (palo == Palo.Comodin) {
            return "Comodín"
        }
        return "$valor de $palo"
    }

    override fun compareTo(other: Carta): Int {
        return valor - other.valor
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(valor)
        parcel.writeSerializable(palo)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Carta> {
        override fun createFromParcel(parcel: Parcel): Carta {
            return Carta(parcel)
        }

        override fun newArray(size: Int): Array<Carta?> {
            return arrayOfNulls(size)
        }
    }
}