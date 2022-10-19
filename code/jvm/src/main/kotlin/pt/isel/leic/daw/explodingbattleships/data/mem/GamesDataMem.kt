package pt.isel.leic.daw.explodingbattleships.data.mem

import pt.isel.leic.daw.explodingbattleships.data.GamesData
import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.*
import java.time.Duration
import java.time.Instant

class GamesDataMem(private val mockData: MockData) : GamesData {
    override fun createGame(
        transaction: Transaction,
        gameType: String,
        player1: Int,
        player2: Int,
        startedAt: Instant
    ): Int {
        val id = mockData.games.maxOf { it.id } + 1
        mockData.games.add(StoredGame(id, gameType, "layout_definition", player1, player2, player1, startedAt))
        return id
    }

    override fun getNumberOfPlayedGames(transaction: Transaction) =
        NumberOfPlayedGames(mockData.games.size)

    override fun getGameState(transaction: Transaction, gameId: Int) =
        mockData.games.find { it.id == gameId }?.state?.let { GameState(it) }

    override fun getGame(transaction: Transaction, gameId: Int): Game? =
        mockData.games.find { it.id == gameId }?.toGame()

    override fun getHitSquares(transaction: Transaction, gameId: Int, playerId: Int): List<VerifiedSquare> =
        mockData.hits.filter { it.game == gameId && it.player == playerId }.map { it.square.toVerifiedSquare() }

    override fun getPlayerGame(transaction: Transaction, playerId: Int): Game? =
        mockData
            .games
            .filter { (it.player1 == playerId || it.player2 == playerId) && it.state != "completed" }
            .map { it.toGame() }.firstOrNull()

    override fun changeCurrPlayer(transaction: Transaction, gameId: Int, newCurrPlayer: Int): Boolean {
        mockData.games.find { it.id == gameId }?.let { game ->
            mockData.games.remove(game)
            val newGame = game.copy(currPlayer = newCurrPlayer)
            return mockData.games.add(newGame)
        }
        return false
    }
}
