package com.example.leoam.chinchonkotlin

import juego.chinchon.*
import junit.framework.Assert.*
import org.junit.Test

class ChinchonKotlinUnitTest {
    @Test
    fun acomodacion_MismoPalo() {
        val palo = Palo.Oro

        val carta1 = Carta(1, palo)
        val carta2 = Carta(2, palo)
        val carta3 = Carta(3, palo)
        val carta4 = Carta(4, palo)
        val carta5 = Carta(5, palo)
        val carta6 = Carta(6, palo)
        val carta7 = Carta(7, palo)

        val mano = Mano()
        mano.addCarta(carta1)
        mano.addCarta(carta2)
        mano.addCarta(carta3)
        mano.addCarta(carta4)
        mano.addCarta(carta5)
        mano.addCarta(carta6)
        mano.addCarta(carta7)

        assertTrue(mano.mismoPalo(intArrayOf(0, 1, 2)))
        assertTrue(mano.mismoPalo(intArrayOf(0, 1, 2, 3)))
        assertTrue(mano.mismoPalo(intArrayOf(0, 1, 2, 3, 4)))
        assertTrue(mano.mismoPalo(intArrayOf(0, 1, 2, 3, 4, 5)))
        assertTrue(mano.mismoPalo(intArrayOf(0, 1, 2, 3, 4, 5, 6)))

        assertFalse(mano.mismoPalo(intArrayOf(0, 1, 3)))
        assertFalse(mano.mismoPalo(intArrayOf(0, 1)))
        assertFalse(mano.mismoPalo(intArrayOf(0, 5, 6)))
    }

    @Test
    fun partida_robarPila() {
        val mazo = Mazo(false)
        val pila = Mazo(true)

        val jugadores = arrayListOf(
                Jugador("Jugador 1"),
                Jugador("Jugador 2"))
        mazo.repartir(jugadores)

        val cartaTirar = jugadores[0].mano.tirarCarta(7)
        pila.colocar(cartaTirar)

        val cartaRobar = pila.robar()
        jugadores[1].mano.addCarta(cartaRobar)

        val octavaCarta = jugadores[1].mano.getCarta(7)

        assertEquals(cartaTirar, cartaRobar)
        assertEquals(cartaRobar, octavaCarta)
    }
}