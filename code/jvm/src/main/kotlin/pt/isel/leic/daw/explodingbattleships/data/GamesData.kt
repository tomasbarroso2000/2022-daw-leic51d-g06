package pt.isel.leic.daw.explodingbattleships.data

import pt.isel.leic.daw.explodingbattleships.domain.Game
import pt.isel.leic.daw.explodingbattleships.domain.GameState
import pt.isel.leic.daw.explodingbattleships.domain.NumberOfPlayedGames
import pt.isel.leic.daw.explodingbattleships.domain.VerifiedSquare

interface GamesData {
    fun createGame(transaction: Transaction, gameType: String, player1: Int, player2: Int): Int

    fun getNumberOfPlayedGames(transaction: Transaction): NumberOfPlayedGames

    fun getGameState(transaction: Transaction, gameId: Int): GameState?

    fun getGame(transaction: Transaction, gameId: Int): Game?

    fun getHitSquares(transaction: Transaction, gameId: Int, playerId: Int): List<VerifiedSquare>

    fun getPlayerGame(transaction: Transaction, playerId: Int): Game?

    fun changeCurrPlayer(transaction: Transaction, gameId: Int, newCurrPlayer: Int): Boolean
}
