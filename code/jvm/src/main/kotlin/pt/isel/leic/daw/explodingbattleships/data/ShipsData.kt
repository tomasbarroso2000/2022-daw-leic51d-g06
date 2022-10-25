package pt.isel.leic.daw.explodingbattleships.data

import pt.isel.leic.daw.explodingbattleships.domain.Ship
import pt.isel.leic.daw.explodingbattleships.domain.ShipDto
import pt.isel.leic.daw.explodingbattleships.domain.ShipState
import pt.isel.leic.daw.explodingbattleships.domain.VerifiedShip
import pt.isel.leic.daw.explodingbattleships.domain.VerifiedSquare

interface ShipsData {
    fun defineLayout(transaction: Transaction, gameId: Int, playerId: Int, ships: List<VerifiedShip>)

    fun checkEnemyLayoutDone(transaction: Transaction, gameId:Int, playerId: Int): Boolean

    fun getShipsAndSquares(transaction: Transaction, gameId: Int, playerId: Int): Map<VerifiedShip, Set<VerifiedSquare>>

    fun updateNumOfHits(transaction: Transaction, gameId: Int, playerId: Int, firstSquare: String)

    fun isShipDestroyed(transaction: Transaction, gameId: Int, playerId: Int, firstSquare: String): Boolean

    fun fleetState(transaction: Transaction, gameId: Int, playerId: Int): List<ShipState>

    fun getFleet(transaction: Transaction, gameId: Int, playerId: Int): List<ShipDto>

    fun getNumOfHits(transaction: Transaction, shipFirstSquare: VerifiedSquare, gameId: Int, playerId: Int): Int

    fun destroyShip(transaction: Transaction, gameId: Int, playerId: Int, firstSquare: VerifiedSquare)

    fun hasShips(transaction: Transaction, playerId: Int, gameId: Int): Boolean
}