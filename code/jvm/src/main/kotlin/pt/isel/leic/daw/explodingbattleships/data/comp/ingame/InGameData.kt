package pt.isel.leic.daw.explodingbattleships.data.comp.ingame

import pt.isel.leic.daw.explodingbattleships.data.comp.transactions.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.*

interface InGameData {
    fun defineLayout(transaction: Transaction, gameId: Int, playerId: Int, ships: List<VerifiedShip>): Boolean

    fun getShipAndSquares(transaction: Transaction, gameId: Int, playerId: Int) : Map<VerifiedShip, Set<VerifiedSquare>>

    fun createHit(transaction: Transaction, square: VerifiedSquare, gameId: Int, playerId: Int): Int

    fun updateNumOfHits(transaction: Transaction, gameId: Int, playerId: Int, shipType: String): Int

    fun isShipDestroyed(transaction: Transaction, gameId: Int, playerId: Int, shipType: String): Boolean

    fun fleetState(transaction: Transaction, gameId: Int, playerId: Int): List<ShipState>

}
