package pt.isel.leic.daw.explodingbattleships.data.mem

import pt.isel.leic.daw.explodingbattleships.data.GamesData
import pt.isel.leic.daw.explodingbattleships.data.Transaction
import pt.isel.leic.daw.explodingbattleships.domain.DataList
import pt.isel.leic.daw.explodingbattleships.domain.Game
import java.time.Instant

class GamesDataMem(private val mockData: MockData) : GamesData {
    override fun createGame(
        transaction: Transaction,
        gameType: String,
        player1: Int,
        player2: Int
    ): Int {
        val id = mockData.games.maxOf { it.id } + 1
        mockData.games.add(Game(id, gameType, "layout_definition", player1, player2, player1, Instant.now()))
        return id
    }

    override fun getGames(transaction: Transaction, userId: Int, limit: Int, skip: Int): DataList<Game> {
        val games = mockData.games.filter {it.player1 == userId || it.player2 == userId }
        return DataList(getSublist(games, limit, skip), hasMore(games.size, limit, skip))
    }


    override fun getNumberOfPlayedGames(transaction: Transaction) =
        mockData.games.size

    override fun getGame(transaction: Transaction, gameId: Int): Game? =
        mockData.games.find { it.id == gameId }

    override fun changeCurrPlayer(transaction: Transaction, gameId: Int, newCurrPlayer: Int) {
        mockData.games.find { it.id == gameId }?.let { game ->
            mockData.games.remove(game)
            val newGame = game.copy(currPlayer = newCurrPlayer, startedAt = Instant.now())
            mockData.games.add(newGame)
        }
    }

    override fun setGameToShooting(transaction: Transaction, gameId: Int) {
        mockData.games.find { it.id == gameId }?.let { game ->
            mockData.games.remove(game)
            val newGame = game.copy(state = "shooting", startedAt = Instant.now())
            mockData.games.add(newGame)
        }
    }

    override fun setGameStateCompleted(transaction: Transaction, gameId: Int) {
        val storedGame = mockData.games.find { it.id == gameId }
        if (storedGame != null) {
            mockData.games.remove(storedGame)
            mockData.games.add(storedGame.copy(state = "completed"))
        }
    }
}