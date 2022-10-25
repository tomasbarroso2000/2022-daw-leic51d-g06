package pt.isel.leic.daw.explodingbattleships.data

import pt.isel.leic.daw.explodingbattleships.domain.Game
import pt.isel.leic.daw.explodingbattleships.domain.Square

interface GamesData {
    fun createGame(transaction: Transaction, gameType: String, player1: Int, player2: Int): Int

    fun getNumberOfPlayedGames(transaction: Transaction): Int

    fun getGameState(transaction: Transaction, gameId: Int): String?

    fun getGame(transaction: Transaction, gameId: Int): Game?

    fun changeCurrPlayer(transaction: Transaction, gameId: Int, newCurrPlayer: Int)

    fun setGameToShooting(transaction: Transaction, gameId: Int)

    fun setGameStateCompleted(transaction: Transaction, gameId: Int)
}
