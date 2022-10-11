package pt.isel.leic.daw.explodingbattleships.data

import pt.isel.leic.daw.explodingbattleships.domain.LayoutOutcome
import pt.isel.leic.daw.explodingbattleships.domain.ShipState
import pt.isel.leic.daw.explodingbattleships.domain.VerifiedShip
import pt.isel.leic.daw.explodingbattleships.domain.VerifiedSquare

interface InGameData {
    fun defineLayout(transaction: Transaction, gameId: Int, playerId: Int, ships: List<VerifiedShip>): LayoutOutcome

    fun getShipAndSquares(transaction: Transaction, gameId: Int, playerId: Int): Map<VerifiedShip, Set<VerifiedSquare>>

    fun createHit(transaction: Transaction, square: VerifiedSquare, gameId: Int, playerId: Int): Boolean

    fun updateNumOfHits(transaction: Transaction, gameId: Int, playerId: Int, shipType: String): Boolean

    fun isShipDestroyed(transaction: Transaction, gameId: Int, playerId: Int, shipType: String): Boolean

    fun fleetState(transaction: Transaction, gameId: Int, playerId: Int): List<ShipState>

    fun getNumOfHits(transaction: Transaction, shipFirstSquare: VerifiedSquare, gameId: Int, playerId: Int): Int

    fun destroyShip(transaction: Transaction, gameId: Int, playerId: Int, firstSquare: VerifiedSquare)
}
