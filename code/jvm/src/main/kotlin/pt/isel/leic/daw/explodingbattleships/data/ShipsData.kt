package pt.isel.leic.daw.explodingbattleships.data

import pt.isel.leic.daw.explodingbattleships.domain.Ship
import pt.isel.leic.daw.explodingbattleships.domain.ShipState
import pt.isel.leic.daw.explodingbattleships.domain.Square

interface ShipsData {
    fun defineLayout(transaction: Transaction, gameId: Int, playerId: Int, ships: List<Ship>)

    fun checkEnemyLayoutDone(transaction: Transaction, gameId:Int, playerId: Int): Boolean

    fun getShipsAndSquares(transaction: Transaction, gameId: Int, playerId: Int): Map<Ship, Set<Square>>

    fun updateNumOfHits(transaction: Transaction, gameId: Int, playerId: Int, firstSquare: String)

    fun isShipDestroyed(transaction: Transaction, gameId: Int, playerId: Int, firstSquare: String): Boolean

    fun getFleet(transaction: Transaction, gameId: Int, playerId: Int): List<Ship>

    fun getShip(transaction: Transaction, firstSquare: String, gameId: Int, playerId: Int): Ship?

    fun destroyShip(transaction: Transaction, gameId: Int, playerId: Int, firstSquare: String)

    fun hasShips(transaction: Transaction, playerId: Int, gameId: Int): Boolean
}