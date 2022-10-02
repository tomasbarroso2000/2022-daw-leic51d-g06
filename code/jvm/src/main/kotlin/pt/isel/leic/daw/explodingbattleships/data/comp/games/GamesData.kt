package pt.isel.leic.daw.explodingbattleships.data.comp.games

import pt.isel.leic.daw.explodingbattleships.data.comp.transactions.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.Game
import pt.isel.leic.daw.explodingbattleships.domain.VerifiedSquare

interface GamesData {
    fun getNumberOfPlayedGames(transaction: Transaction): Int

    fun getGameState(transaction: Transaction, gameId: Int): String?

    fun getGame(transaction: Transaction, gameId: Int): Game?

    fun getHitSquares(transaction: Transaction, gameId: Int, playerId: Int): List<VerifiedSquare>?
}