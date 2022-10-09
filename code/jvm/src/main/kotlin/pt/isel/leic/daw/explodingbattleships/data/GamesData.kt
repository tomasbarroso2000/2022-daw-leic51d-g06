package pt.isel.leic.daw.explodingbattleships.data

import pt.isel.leic.daw.explodingbattleships.domain.Game
import pt.isel.leic.daw.explodingbattleships.domain.VerifiedSquare

interface GamesData {
    fun getNumberOfPlayedGames(transaction: Transaction): Int

    fun getGameState(transaction: Transaction, gameId: Int): String?

    fun getGame(transaction: Transaction, gameId: Int): Game?

    fun getHitSquares(transaction: Transaction, gameId: Int, playerId: Int): List<VerifiedSquare>

    fun getPlayerGame(transaction: Transaction, playerId: Int): Game?

    fun changeCurrPlayer(transaction: Transaction, gameId: Int, newCurrPlayer: Int): Boolean
}
