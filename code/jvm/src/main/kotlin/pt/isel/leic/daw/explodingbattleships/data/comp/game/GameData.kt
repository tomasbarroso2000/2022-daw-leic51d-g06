package pt.isel.leic.daw.explodingbattleships.data.comp.game

import pt.isel.leic.daw.explodingbattleships.data.comp.transactions.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.Game
import pt.isel.leic.daw.explodingbattleships.domain.Ship
import pt.isel.leic.daw.explodingbattleships.domain.Square
import java.time.LocalDate

interface GameData {
    fun getNumberOfPlayedGames(transaction: Transaction): Int

    fun getGameState(transaction: Transaction, gameId: Int): String?

    fun getGame(transaction: Transaction, gameId: Int): Game?

    fun defineLayout(transaction: Transaction, gameId: Int, playerId: Int, ships: List<Ship>): Boolean

    fun sendShots(transaction: Transaction) // TODO: add parameters

    fun playerFleetState(transaction: Transaction) // TODO: add parameters

    fun enemyFleetState(transaction: Transaction) // TODO: add parameters

    fun squareHit(transaction: Transaction, square: Square, hitTimestamp: LocalDate, playerId: Int, gameId: Int) : Boolean
}
