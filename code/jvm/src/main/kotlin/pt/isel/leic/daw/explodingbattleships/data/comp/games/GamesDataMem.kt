package pt.isel.leic.daw.explodingbattleships.data.comp.games

import pt.isel.leic.daw.explodingbattleships.data.comp.transactions.Transaction
import pt.isel.leic.daw.explodingbattleships.data.comp.utils.MockData
import pt.isel.leic.daw.explodingbattleships.data.comp.utils.toGame
import pt.isel.leic.daw.explodingbattleships.domain.Game
import pt.isel.leic.daw.explodingbattleships.domain.VerifiedSquare
import pt.isel.leic.daw.explodingbattleships.domain.toVerifiedSquare

class GamesDataMem(private val mockData: MockData) : GamesData {
    override fun getNumberOfPlayedGames(transaction: Transaction): Int = mockData.games.size

    override fun getGameState(transaction: Transaction, gameId: Int): String? =
        mockData.games.find { it.id == gameId }?.state

    override fun getGame(transaction: Transaction, gameId: Int): Game? =
        mockData.games.find { it.id == gameId }?.toGame()

    override fun getHitSquares(transaction: Transaction, gameId: Int, playerId: Int): List<VerifiedSquare> =
        mockData.hits.filter { it.game == gameId && it.player == playerId }.map { it.square.toVerifiedSquare() }

    override fun getPlayerGame(transaction: Transaction, playerId: Int): Game? =
        mockData
            .games
            .filter { (it.player1 == playerId || it.player2 == playerId) && it.state != "completed" }
            .map { it.toGame() }.firstOrNull()
}