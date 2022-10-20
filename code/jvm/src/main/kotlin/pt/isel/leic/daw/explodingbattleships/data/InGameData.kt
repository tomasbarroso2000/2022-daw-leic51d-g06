package pt.isel.leic.daw.explodingbattleships.data

import pt.isel.leic.daw.explodingbattleships.domain.*

interface InGameData {
    fun defineLayout(transaction: Transaction, gameId: Int, playerId: Int, ships: List<VerifiedShip>): Boolean

    fun checkEnemyDone(transaction: Transaction, gameId:Int, playerId: Int): Boolean

    fun startGame(transaction: Transaction, gameId: Int, playerId: Int): LayoutOutcome

    fun getShipAndSquares(transaction: Transaction, gameId: Int, playerId: Int): Map<VerifiedShip, Set<VerifiedSquare>>

    fun createHit(transaction: Transaction, square: VerifiedSquare, gameId: Int, playerId: Int): Boolean

    fun updateNumOfHits(transaction: Transaction, gameId: Int, playerId: Int, firstSquare: String): Boolean

    fun isShipDestroyed(transaction: Transaction, gameId: Int, playerId: Int, firstSquare: String): Boolean

    fun fleetState(transaction: Transaction, gameId: Int, playerId: Int): List<ShipState>

    fun getNumOfHits(transaction: Transaction, shipFirstSquare: VerifiedSquare, gameId: Int, playerId: Int): Int

    fun destroyShip(transaction: Transaction, gameId: Int, playerId: Int, firstSquare: VerifiedSquare): Boolean

    fun hasShips(transaction: Transaction, playerId: Int, gameId: Int): Boolean

    fun setGameStateCompleted(transaction: Transaction, gameId: Int): Boolean
}
