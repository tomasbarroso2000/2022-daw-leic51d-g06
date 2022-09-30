package pt.isel.leic.daw.explodingbattleships.data.comp.games

import pt.isel.leic.daw.explodingbattleships.data.comp.transactions.Transaction
import pt.isel.leic.daw.explodingbattleships.data.comp.utils.MockData
import pt.isel.leic.daw.explodingbattleships.domain.Game
import pt.isel.leic.daw.explodingbattleships.domain.Square

class GamesDataMem(private val mockData: MockData) : GamesData {
    override fun getNumberOfPlayedGames(transaction: Transaction): Int = mockData.games.size

    override fun getGameState(transaction: Transaction, gameId: Int): String? =
        mockData.games.find { it.id == gameId }?.state

    override fun getGame(transaction: Transaction, gameId: Int): Game? {
        TODO("Not yet implemented")
    }

    override fun getHitSquares(transaction: Transaction, gameId: Int, playerId: Int): List<Square>? {
        TODO("Not yet implemented")
    }
}