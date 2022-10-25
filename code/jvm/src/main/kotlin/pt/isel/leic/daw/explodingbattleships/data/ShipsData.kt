package pt.isel.leic.daw.explodingbattleships.data

import pt.isel.leic.daw.explodingbattleships.domain.Ship
import pt.isel.leic.daw.explodingbattleships.domain.Square

interface ShipsData {
    fun defineLayout(transaction: Transaction, gameId: Int, userId: Int, ships: List<Ship>)

    fun checkEnemyLayoutDone(transaction: Transaction, gameId: Int, userId: Int): Boolean

    fun getShipsAndSquares(transaction: Transaction, gameId: Int, userId: Int): Map<Ship, Set<Square>>

    fun updateNumOfHits(transaction: Transaction, gameId: Int, userId: Int, firstSquare: String)

    fun isShipDestroyed(transaction: Transaction, gameId: Int, userId: Int, firstSquare: String): Boolean

    fun getFleet(transaction: Transaction, gameId: Int, userId: Int): List<Ship>

    fun getShip(transaction: Transaction, firstSquare: String, gameId: Int, userId: Int): Ship?

    fun destroyShip(transaction: Transaction, gameId: Int, userId: Int, firstSquare: String)

    fun hasShips(transaction: Transaction, userId: Int, gameId: Int): Boolean
}
